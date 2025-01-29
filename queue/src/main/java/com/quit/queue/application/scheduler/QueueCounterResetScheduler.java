package com.quit.queue.application.scheduler;

import com.quit.queue.infrastructure.util.ServerInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueCounterResetScheduler {
    private final ServerInfo serverInfo;

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Scheduled(cron = "0 0 3,15 * * ?")
    public void resetQueueCounters() {
        String serverId = serverInfo.getServerId();
        String storesKey = "queue:server:" + serverId + ":stores";

        Mono.fromRunnable(() -> {
                    reactiveRedisTemplate.hasKey(storesKey)
                            .flatMap(exists -> {
                                if (!exists) {
                                    return Mono.empty();
                                }

                                return reactiveRedisTemplate.opsForSet().members(storesKey)
                                        .flatMap(storeId -> {
                                            String counterKey = "queue:store:" + storeId + ":counter";
                                            String queueKey = "queue:store:" + storeId + ":users";

                                            return reactiveRedisTemplate.opsForZSet().size(queueKey)
                                                    .flatMap(size -> {
                                                        if (size == 0) {
                                                            return reactiveRedisTemplate.delete(counterKey)
                                                                    .then(Mono.empty());
                                                        }
                                                        return Mono.empty();
                                                    });
                                        })
                                        .then();
                            })
                            .doOnError(error -> log.error("Error resetting queue counters: {}", error.getMessage()))
                            .subscribe();
                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }
}