package com.quit.queue.application.service;

import com.quit.queue.application.dto.res.QueueResponse;
import com.quit.queue.common.ApiResponse;
import com.quit.queue.common.RoleValidationType;
import com.quit.queue.presentation.request.ReservationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.*;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class QueueServiceTest {

    @Mock
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Mock
    private ReactiveValueOperations<String, String> valueOperations;

    @Mock
    private ReactiveZSetOperations<String, String> zSetOperations;

    @Mock
    private ReactiveHashOperations<String, Object, Object> hashOperations;

    @Mock
    private StoreService storeService;

    @Mock
    private RoleValidationService roleValidationService;

    @InjectMocks
    private QueueService queueService;

    @BeforeEach
    void setUp() {
        when(reactiveRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(reactiveRedisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(reactiveRedisTemplate.opsForHash()).thenReturn(hashOperations);

        when(hashOperations.putAll(any(), any()))
                .thenReturn(Mono.empty());

        when(zSetOperations.add(anyString(), anyString(), anyDouble())).thenReturn(Mono.just(true));
        when(valueOperations.set(any(), any())).thenReturn(Mono.just(true));
        when(roleValidationService.validateUserRole(any(), anyInt())).thenReturn(Mono.empty());
        when(storeService.getStoreForInternal(any(UUID.class))).thenReturn(Mono.just(true));

        when(reactiveRedisTemplate.expire(anyString(), any(Duration.class)))
                .thenReturn(Mono.just(true));
    }

    @Test
    void testAddUserToQueueForStore() {
        // Given
        UUID storeId = UUID.randomUUID();
        ReservationRequest reservationRequest = new ReservationRequest(
                4,
                LocalDate.of(2025, 1, 12),
                LocalTime.of(14, 0)
        );
        String userId = "user123";
        String userEmail = "user@example.com";
        String userRole = "ROLE_USER";

        Mockito.when(roleValidationService.validateUserRole(userRole, RoleValidationType.USER))
                .thenReturn(Mono.empty());

        Mockito.when(storeService.getStoreForInternal(storeId))
                .thenReturn(Mono.just(true));

        String userQueueKey = "queue:user:" + userId;
        Mockito.when(reactiveRedisTemplate.opsForValue().get(userQueueKey))
                .thenReturn(Mono.empty());

        String key = "queue:store:" + storeId + ":users";
        Mockito.when(reactiveRedisTemplate.opsForZSet().reverseRangeWithScores(key, Range.closed(0L, 0L)))
                .thenReturn(Flux.just(new DefaultTypedTuple<>(userId, 1.0)));

        Mockito.when(reactiveRedisTemplate.opsForZSet().rank(key, userId))
                .thenReturn(Mono.empty());

        Mockito.when(reactiveRedisTemplate.opsForHash().putAll(any(), any()))
                .thenReturn(Mono.empty());

        // When
        Mono<ApiResponse<?>> result = queueService.addUserToQueueForStore(storeId, reservationRequest, userId, userEmail, userRole)
                .switchIfEmpty(Mono.just(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "User not found in the queue")));

        // Then
        StepVerifier.create(result)
                .expectNextMatches(apiResponse -> {
                    assertThat(apiResponse).isNotNull();
                    assertThat(apiResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(apiResponse.getMessage()).isEqualTo("User not found in the queue");
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void getQueueForStore() {
        // Given
        UUID storeId = UUID.randomUUID();
        String key = "queue:store:" + storeId + ":users";

        List<ZSetOperations.TypedTuple<String>> mockQueue = List.of(
                createTypedTuple("1", 10.0),
                createTypedTuple("2", 20.0),
                createTypedTuple("3", 30.0)
        );

        when(reactiveRedisTemplate.hasKey(key)).thenReturn(Mono.just(true));
        when(reactiveRedisTemplate.opsForZSet().rangeWithScores(eq(key), any(Range.class)))
                .thenReturn(Flux.fromIterable(mockQueue));

        // When
        Mono<ApiResponse<?>> actualMonoResponse = queueService.getQueue(storeId, "ROLE_MASTER");

        // Then
        ApiResponse<QueueResponse> actualApiResponse = (ApiResponse<QueueResponse>) actualMonoResponse.block();

        assertNotNull(actualApiResponse, "ApiResponse should not be null");
        assertEquals(actualApiResponse.getCode(), HttpStatus.OK.value(), "Response code should be OK");

        QueueResponse queueResponse = actualApiResponse.getData();

        assertNotNull(queueResponse, "QueueResponse should not be null");
        assertEquals(storeId, queueResponse.getStoreId(), "StoreId should match");
    }

    @Test
    void testRemoveUserFromQueueForStore_Success() {
        // Given
        UUID storeId = UUID.randomUUID();
        String paramUserId = "user123";
        String userId = "user123";
        String userRole = "ROLE_USER";
        String userQueueKey = "queue:user:" + userId;
        String key = "queue:store:" + storeId + ":users";
        String refreshKey = "queue:store:" + storeId + ":refresh:" + userId;

        when(roleValidationService.validateUserRole(userRole, 2)).thenReturn(Mono.empty());

        when(reactiveRedisTemplate.opsForValue().get(userQueueKey))
                .thenReturn(Mono.just(storeId.toString()))
                .thenReturn(Mono.empty());

        when(reactiveRedisTemplate.opsForZSet().remove(key, userId))
                .thenReturn(Mono.just(1L));

        when(reactiveRedisTemplate.delete(userQueueKey, refreshKey)).thenReturn(Mono.empty());

        // When
        Mono<ApiResponse<Object>> result = queueService.removeUserFromQueueForStore(storeId, paramUserId, userId, userRole)
                .doOnError(error -> System.out.println("Error occurred: " + error.getMessage()))
                .doOnTerminate(() -> System.out.println("Completed removeUserFromQueueForStore"))
                .doOnNext(response -> System.out.println("Response: " + response.getMessage()));

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> {
                    System.out.println("Response: " + response.getMessage());
                    assertEquals("User removed from queue successfully", response.getMessage());
                    return true;
                })
                .verifyComplete();

        verify(reactiveRedisTemplate).delete(userQueueKey, refreshKey);
    }

    private ZSetOperations.TypedTuple<String> createTypedTuple(String value, double score) {
        return new DefaultTypedTuple<>(value, score);
    }
}