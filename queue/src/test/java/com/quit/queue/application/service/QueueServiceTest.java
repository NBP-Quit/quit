package com.quit.queue.application.service;

import com.quit.queue.application.dto.res.QueueResponse;
import com.quit.queue.common.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.*;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

        queueService.addUserToQueueForStore(storeId, null, userId).block();  // 비동기 메서드를 block으로 동기화하여 실행

        // Then
        // ZSet에 추가되는지 확인
        verify(zSetOperations).add(key, userId.toString(), 1.0);  // Double 타입 사용
    }

    @Test
    void getQueueForStore() {
        // Given
        UUID storeId = UUID.randomUUID();
        String key = "queue:store:" + storeId + ":users";

        // Mock된 데이터 (ZSet의 항목과 점수)
        List<ZSetOperations.TypedTuple<String>> mockQueue = List.of(
                createTypedTuple("1", 10.0),
                createTypedTuple("2", 20.0),
                createTypedTuple("3", 30.0)
        );

        // Mocking Redis의 hasKey와 opsForZSet rangeWithScores 메소드
        when(reactiveRedisTemplate.hasKey(key)).thenReturn(Mono.just(true));
        when(reactiveRedisTemplate.opsForZSet().rangeWithScores(eq(key), any(Range.class)))
                .thenReturn(Flux.fromIterable(mockQueue));

        // When
        Mono<ApiResponse<?>> actualMonoResponse = queueService.getQueue(storeId);

        // Then: Mono<ApiResponse<QueueResponse>>로 받으므로 바로 사용
        ApiResponse<QueueResponse> actualApiResponse = (ApiResponse<QueueResponse>) actualMonoResponse.block();  // block()을 사용하여 Mono를 동기적으로 처리

        // 반환된 ApiResponse의 성공 여부 확인
        assertNotNull(actualApiResponse, "ApiResponse should not be null");
        assertEquals(actualApiResponse.getCode(), HttpStatus.OK.value(), "Response code should be OK");

        // QueueResponse 데이터 추출
        QueueResponse queueResponse = actualApiResponse.getData();  // getData()로 QueueResponse 객체를 받음

        assertNotNull(queueResponse, "QueueResponse should not be null");
        assertEquals(storeId, queueResponse.getStoreId(), "StoreId should match");
    }

    // Helper method for creating TypedTuple
    private ZSetOperations.TypedTuple<String> createTypedTuple(String value, double score) {
        return new DefaultTypedTuple<>(value, score);
    }
}