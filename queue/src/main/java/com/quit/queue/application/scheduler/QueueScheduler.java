package com.quit.queue.application.scheduler;

import com.quit.queue.application.messaging.ReservationMessage;
import com.quit.queue.infrastructure.messaging.KafkaMessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueScheduler {
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    private final KafkaMessageProducer kafkaMessageProducer;

    @Scheduled(fixedRate = 5000)
    public void processQueues() {
        Flux<String> queueKeys = reactiveRedisTemplate.keys("queue:store:*:users");

        queueKeys.parallel()
                .runOn(Schedulers.parallel())
                .flatMap(this::processQueue)
                .sequential()
                .subscribe();
    }

    private Mono<Void> processQueue(String queueKey) {
        UUID storeId = UUID.fromString(queueKey.split(":")[2]);

        return reactiveRedisTemplate.opsForZSet().rangeWithScores(queueKey, Range.closed(0L, 499L))
                .collectList()
                .flatMap(entries -> {
                    if (entries.isEmpty()) {
                        return Mono.empty();
                    }

                    return Flux.fromIterable(entries)
                            .flatMap(entry -> {
                                String userId = entry.getValue();
                                String reservationKey = "queue:store:" + storeId + ":reservations:" + userId;

                                return reactiveRedisTemplate.opsForHash().multiGet(reservationKey, Arrays.asList("guestCount", "reservationDate", "reservationTime"))
                                        .flatMap(values -> {
                                            if (values.size() != 3 || values.contains(null)) {
                                                return Mono.empty();
                                            }

                                            ReservationMessage reservationMessage = ReservationMessage.of(
                                                    userId, storeId.toString(), (String) values.get(0), (String) values.get(1), (String) values.get(2));
                                            return sendToReservationService(reservationMessage);
                                        });
                            })
                            .then(removeUsersFromQueue(queueKey, entries));
                });
    }

    private Mono<Void> sendToReservationService(ReservationMessage reservation) {
        String key = reservation.getStoreId() + ":" + reservation.getUserId();
        return kafkaMessageProducer.sendMessage("queue.reservation", key, reservation);
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
            String userQueueKey = "queue:user:" + userId;

            removalTasks.add(reactiveRedisTemplate.delete(reservationKey).then());
            removalTasks.add(reactiveRedisTemplate.delete(userQueueKey).then());
        });

        return Flux.concat(removalTasks)
                .then(reactiveRedisTemplate.opsForZSet().remove(queueKey, userIds.toArray()))
                .then();
    }

}
