package com.quit.queue.application.scheduler;

import com.quit.queue.application.messaging.ReservationMessage;
import com.quit.queue.infrastructure.messaging.KafkaMessageProducer;
import com.quit.queue.infrastructure.util.ServerInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QueueScheduler {
    private final ServerInfo serverInfo;

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    private final KafkaMessageProducer kafkaMessageProducer;

    @Scheduled(fixedRate = 10000)
    public void processQueues() {
        String serverId = serverInfo.getServerId();
        String storesKey = "queue:server:" + serverId + ":stores";

        reactiveRedisTemplate.opsForSet().members(storesKey)
                .flatMap(storeId -> {
                    String queueKey = "queue:store:" + storeId + ":users";
                    return processQueue(queueKey);
                })
                .subscribe();
    }

    private Mono<Void> processQueue(String queueKey) {
        String storeId = queueKey.split(":")[2];
        String statusKey = "queue:store:" + storeId + ":status";

        return reactiveRedisTemplate.opsForValue().setIfAbsent(statusKey, "processing")
                .flatMap(lockAcquired -> {
                    if (lockAcquired) {
                        return reactiveRedisTemplate.opsForZSet().rangeWithScores(queueKey, Range.closed(0L, 999L))
                                .collectList()
                                .flatMap(entries -> {
                                    if (entries.isEmpty()) {
                                        return Mono.empty();
                                    }

                                    return Flux.fromIterable(entries)
                                            .flatMap(entry -> {
                                                String userId = entry.getValue();
                                                String reservationKey = "queue:store:" + storeId + ":reservations:" + userId;
                                                String refreshKey = "queue:store:" + storeId + ":refresh:" + userId;

                                                return reactiveRedisTemplate.hasKey(refreshKey)
                                                        .flatMap(refreshExists -> {
                                                            if (!refreshExists) {
                                                                return Mono.empty();
                                                            }

                                                            return reactiveRedisTemplate.opsForHash().multiGet(reservationKey, Arrays.asList("userEmail", "guestCount", "reservationDate", "reservationTime"))
                                                                    .flatMap(values -> {
                                                                        if (values.size() != 4 || values.contains(null)) {
                                                                            return Mono.empty();
                                                                        }

                                                                        ReservationMessage reservationMessage = ReservationMessage.of(
                                                                                userId, (String) values.get(0), storeId, (String) values.get(1), (String) values.get(2), (String) values.get(3));
                                                                        return sendToReservationService(reservationMessage);
                                                                    });
                                                        });
                                            })
                                            .then(removeUsersFromQueue(queueKey, entries));
                                })
                                .publishOn(Schedulers.boundedElastic())
                                .doFinally(signalType -> reactiveRedisTemplate.delete(statusKey).subscribe());
                    } else {
                        return Mono.empty();
                    }
                });
    }

    private Mono<Void> sendToReservationService(ReservationMessage reservation) {
        String key = reservation.getStoreId() + ":" + reservation.getUserId();
        return kafkaMessageProducer.sendMessage("queue.process.success", key, reservation);
    }

    private Mono<Void> removeUsersFromQueue(String queueKey, List<ZSetOperations.TypedTuple<String>> entries) {
        String[] parts = queueKey.split(":");
        UUID storeId = UUID.fromString(parts[2]);

        List<String> userIds = entries.stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .toList();

        List<Mono<Void>> removalTasks = new ArrayList<>();

        userIds.forEach(userId -> {
            String reservationKey = "queue:store:" + storeId + ":reservations:" + userId;
            String refreshKey = "queue:store:" + storeId + ":refresh:" + userId;
            String userQueueKey = "queue:user:" + userId;

            removalTasks.add(reactiveRedisTemplate.delete(reservationKey).then());
            removalTasks.add(reactiveRedisTemplate.delete(refreshKey).then());
            removalTasks.add(reactiveRedisTemplate.delete(userQueueKey).then());
        });

        return Flux.concat(removalTasks)
                .then(reactiveRedisTemplate.opsForZSet().remove(queueKey, userIds.toArray()))
                .then();
    }

}
