package com.quit.queue.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QueueService {
    private final RedisTemplate<String, String> redisTemplate;

    // 대기열에 사용자 추가
    public void addUserToQueue(UUID storeId, Long userId) {
        String counterKey = "queue:store:" + storeId + ":counter";
        double newScore = redisTemplate.opsForValue().increment(counterKey, 1);

        String key = "queue:store:" + storeId + ":users";
        redisTemplate.opsForZSet().add(key, userId.toString(), newScore);
    }

    // store 별 대기열 조회
    public Set<String> getQueueForStore(UUID storeId) {
        String key = "queue:store:" + storeId + ":users";

        return redisTemplate.opsForZSet().range(key, 0, -1);
    }
}
