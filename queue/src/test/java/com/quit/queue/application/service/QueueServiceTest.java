package com.quit.queue.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class QueueServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @InjectMocks
    private QueueService queueService;

    @BeforeEach
    void setUp() {
        // Mockito 초기화
        MockitoAnnotations.openMocks(this);
        // redisTemplate의 opsForValue()와 opsForZSet()가 각각 Mock 객체를 반환하도록 설정
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    @Test
    void addUserToQueue() {
        // Given
        UUID storeId = UUID.randomUUID();
        Long userId = 1L;
        String counterKey = "queue:store:" + storeId + ":counter";
        String key = "queue:store:" + storeId + ":users";

        // When
        when(valueOperations.increment(counterKey, 1)).thenReturn((long) 1.0);  // Counter 값 증가 결과
        queueService.addUserToQueue(storeId, userId);

        // Then
        // ZSet에 추가되는지 확인
        verify(zSetOperations).add(key, userId.toString(), 1.0);  // Double 타입 사용
    }

    @Test
    void getQueueForStore() {
        // Given
        UUID storeId = UUID.randomUUID();
        String key = "queue:store:" + storeId + ":users";
        Set<String> expectedQueue = Set.of("user1", "user2", "user3");

        // When
        when(redisTemplate.opsForZSet().range(key, 0, -1)).thenReturn(expectedQueue);

        // Then
        Set<String> actualQueue = queueService.getQueueForStore(storeId);
        assertEquals(expectedQueue, actualQueue);  // 반환된 값이 예상값과 일치하는지 확인
    }
}
