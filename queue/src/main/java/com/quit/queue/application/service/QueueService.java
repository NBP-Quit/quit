package com.quit.queue.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QueueService {
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    // 대기열에 사용자 추가
    public Mono<Long> addUserToQueue(UUID storeId, Long userId) {
        String counterKey = "queue:store:" + storeId + ":counter";
        String key = "queue:store:" + storeId + ":users";

        // 사용자 추가 및 카운터 값 반환
        return reactiveRedisTemplate.opsForValue().increment(counterKey, 1)
                .flatMap(newScore -> reactiveRedisTemplate.opsForZSet().add(key, userId.toString(), newScore)
                        .thenReturn(newScore)); // newScore를 반환
    }

    // store 별 대기열 조회
    public Flux<String> getQueueForStore(UUID storeId) {
        String key = "queue:store:" + storeId + ":users";

        return reactiveRedisTemplate.opsForZSet().range(key, Range.closed(0L, -1L));
    }
}
