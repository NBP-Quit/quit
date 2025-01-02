package com.quit.queue.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.data.redis.core.ReactiveZSetOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class QueueServiceTest {

    @Mock
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Mock
    private ReactiveValueOperations<String, String> valueOperations;

    @Mock
    private ReactiveZSetOperations<String, String> zSetOperations;

    @InjectMocks
    private QueueService queueService;

    @BeforeEach
    void setUp() {
        // Mockito 초기화
        MockitoAnnotations.openMocks(this);
        // redisTemplate의 opsForValue()와 opsForZSet()가 각각 Mock 객체를 반환하도록 설정
        when(reactiveRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(reactiveRedisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    @Test
    void addUserToQueue() {
        // Given
        UUID storeId = UUID.randomUUID();
        Long userId = 1L;
        String counterKey = "queue:store:" + storeId + ":counter";
        String key = "queue:store:" + storeId + ":users";

        // When
        when(valueOperations.increment(counterKey, 1)).thenReturn(Mono.just(1L));  // Mono로 반환하도록 수정
        when(zSetOperations.add(key, userId.toString(), 1.0)).thenReturn(Mono.just(true));  // Mocking Mono<Boolean>

        queueService.addUserToQueue(storeId, userId).block();  // 비동기 메서드를 block으로 동기화하여 실행

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
        when(reactiveRedisTemplate.opsForZSet().range(key, Range.closed(0L, -1L))).thenReturn(Flux.just("user1", "user2", "user3"));

        // Then
        Set<String> actualQueue = queueService.getQueueForStore(storeId)
                .collect(Collectors.toSet())  // Flux를 Set으로 변환
                .block();  // 동기화

        assertEquals(expectedQueue, actualQueue);  // 반환된 값이 예상값과 일치하는지 확인
    }


}