package com.quit.queue.application.service;

import com.quit.queue.application.service.dto.res.QueueResponse;
import com.quit.queue.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    public Mono<ApiResponse<?>> addUserToQueueForStore(UUID storeId, Long userId) {
        // TODO 권한 검증 추가

        String key = "queue:store:" + storeId + ":users";
        String userQueueKey = "queue:user:" + userId;

        return reactiveRedisTemplate.opsForValue().get(userQueueKey)
                .switchIfEmpty(Mono.just(""))
                .flatMap(currentQueue -> {
                    if (currentQueue.equals(storeId.toString())) {
                        return Mono.just(ApiResponse.success("User is already in the current queue"));
                    } else if (!currentQueue.isEmpty() && !currentQueue.equals(storeId.toString())) {
                        return Mono.error(new IllegalStateException("User is already in another queue"));
                    } else {
                        return reactiveRedisTemplate.opsForValue().set(userQueueKey, storeId.toString())
                                .then(reactiveRedisTemplate.opsForZSet().reverseRangeWithScores(key, Range.closed(0L, 0L))
                                        .next()
                                        .map(ZSetOperations.TypedTuple::getScore)
                                        .switchIfEmpty(Mono.just(0.0))
                                        .flatMap(highestScore -> {
                                            float newScore = (highestScore == 0.0) ? 1.0f : (float) (highestScore + 1);
                                            return reactiveRedisTemplate.opsForZSet().add(key, userId.toString(), newScore)
                                                    .then(reactiveRedisTemplate.opsForZSet().rank(key, userId.toString()))
                                                    .flatMap(rank -> {
                                                        if (rank == null) {
                                                            return Mono.error(new IllegalStateException("Failed to get rank"));
                                                        }
                                                        return Mono.just(ApiResponse.success(rank + 1));
                                                    });
                                        })
                                );
                    }
                });
    }

    public Mono<ApiResponse<Float>> getUserPositionInQueueForStore(UUID storeId, Long userId) {
        // TODO 권한 검증 추가

        String key = "queue:store:" + storeId + ":users";

        return reactiveRedisTemplate.opsForZSet().score(key, userId.toString())
                .flatMap(score -> {
                    if (score == null) {
                        return Mono.error(new IllegalStateException("User not found in queue"));
                    }
                    return Mono.just(ApiResponse.success(score.floatValue()));
                });
    }

    public Mono<ApiResponse<?>> getQueue(UUID storeId) {
        // TODO 권한 검증 추가

        return (storeId == null)
                ? getAllQueues().map(ApiResponse::success)
                : getQueueForStore(storeId);
    }

    private Mono<List<QueueResponse>> getAllQueues() {
        return reactiveRedisTemplate.keys("queue:store:*:users")
                .flatMap(key -> {
                    UUID storeId = UUID.fromString(key.split(":")[2]);
                    return reactiveRedisTemplate.opsForZSet().rangeWithScores(key, Range.closed(0L, -1L))
                            .collectList()
                            .map(entries -> {
                                QueueResponse response = new QueueResponse(storeId);
                                entries.forEach(entry ->
                                        response.addUserScore(Long.valueOf(entry.getValue()), entry.getScore().floatValue()));
                                return response;
                            });
                })
                .collectList();
    }

    private Mono<ApiResponse<?>> getQueueForStore(UUID storeId) {
        String key = "queue:store:" + storeId + ":users";

        return reactiveRedisTemplate.hasKey(key)
                .flatMap(exists -> {
                    if (exists) {
                        return reactiveRedisTemplate.opsForZSet().rangeWithScores(key, Range.closed(0L, -1L))
                                .collectList()
                                .map(entries -> {
                                    QueueResponse response = new QueueResponse(storeId);
                                    entries.forEach(entry ->
                                            response.addUserScore(Long.valueOf(entry.getValue()), entry.getScore().floatValue()));
                                    return response;
                                })
                                .map(ApiResponse::success);
                    } else {
                        return Mono.error(new IllegalArgumentException("Store queue not found for storeId: " + storeId));
                    }
                });
    }

    public Mono<ApiResponse<Object>> removeUserFromQueueForStore(UUID storeId, Long userId) {
        // TODO 권한 검증 추가

        String key = "queue:store:" + storeId + ":users";
        String userQueueKey = "queue:user:" + userId;
        String refreshKey = "queue:store:" + storeId + ":refresh:" + userId;

        return reactiveRedisTemplate.opsForValue().get(userQueueKey)
                .flatMap(currentQueue -> {
                    if (currentQueue != null && !currentQueue.equals(storeId.toString())) {
                        return Mono.error(new IllegalStateException("User not in the specified store queue"));
                    }

                    return reactiveRedisTemplate.opsForZSet().remove(key, userId.toString())
                            .flatMap(result -> {
                                if (result > 0) {
                                    return reactiveRedisTemplate.delete(userQueueKey, refreshKey)
                                            .then(Mono.just(ApiResponse.success("User removed from queue successfully")));
                                }
                                return Mono.error(new IllegalArgumentException("User not found in queue"));
                            });
                })
                .onErrorResume(e -> Mono.just(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Failed to remove user from queue: " + e.getMessage())));
    }

    public Mono<ApiResponse<Object>> resetQueueForStore(UUID storeId) {
        // TODO 권한 검증 추가

        return (storeId == null) ? resetAllQueues() : resetStoreQueue(storeId);
    }

    private Mono<ApiResponse<Object>> resetAllQueues() {
        // 모든 관련 키를 삭제 (queue:store:* 및 queue:user:*)
        return reactiveRedisTemplate.scan(ScanOptions.scanOptions().match("queue:*").build())
                .flatMap(reactiveRedisTemplate::delete)
                .then(Mono.just(ApiResponse.success("All queues have been reset successfully.")))
                .onErrorMap(e -> new RuntimeException("Failed to reset all queues", e));
    }

    private Mono<ApiResponse<Object>> resetStoreQueue(UUID storeId) {
        String key = "queue:store:" + storeId + ":users";

        return reactiveRedisTemplate.opsForZSet().range(key, Range.closed(0L, -1L))
                .filter(queuedUserId -> queuedUserId != null && !queuedUserId.isEmpty())
                .flatMapSequential(queuedUserId -> removeFromUserQueueAndRefreshKey(storeId, queuedUserId))
                .then(reactiveRedisTemplate.delete(key))
                .then(Mono.just(ApiResponse.success("Store queue has been reset successfully.")))
                .onErrorMap(e -> new RuntimeException("Failed to reset store queue for storeId: " + storeId, e));
    }

    private Mono<Void> removeFromUserQueueAndRefreshKey(UUID storeId, String queuedUserId) {
        String userQueueKey = "queue:user:" + queuedUserId;
        String refreshKey = "queue:store:" + storeId + ":refresh:" + queuedUserId;

        return reactiveRedisTemplate.delete(userQueueKey, refreshKey)
                .doOnSuccess(count -> log.debug("Removed user {} and its refresh key from queue info", queuedUserId))
                .onErrorMap(e -> new RuntimeException("Failed to remove " + queuedUserId + " from its queue info.", e))
                .then();
    }

    public Mono<ApiResponse<Integer>> checkUserInQueueForStore(UUID storeId, Long userId) {
        // TODO 권한 검증 추가

        String queueKey = "queue:store:" + storeId + ":users";
        String refreshKey = "queue:store:" + storeId + ":refresh:" + userId;

        return reactiveRedisTemplate.opsForZSet().rank(queueKey, userId.toString())
                .flatMap(rank -> {
                    if (rank == null) {
                        return Mono.error(new IllegalStateException("User not found in queue"));
                    }

                    return reactiveRedisTemplate.opsForValue().set(refreshKey, String.valueOf(System.currentTimeMillis()))
                            .then(reactiveRedisTemplate.expire(refreshKey, Duration.ofMinutes(5)))
                            .then(Mono.just(ApiResponse.success(rank.intValue() + 1)));
                });
    }
}