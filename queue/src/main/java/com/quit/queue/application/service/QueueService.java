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

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    public Mono<ApiResponse<Float>> addUserToQueueForStore(UUID storeId, Long userId) {
        // TODO 권한 검증 추가

        String key = "queue:store:" + storeId + ":users";
        String globalUserKey = "queue:global:users";

        return reactiveRedisTemplate.opsForSet().isMember(globalUserKey, userId.toString())
                .flatMap(isMember -> {
                    if (isMember) {
                        return Mono.error(new IllegalStateException("User already in another queue"));
                    } else {
                        return reactiveRedisTemplate.opsForSet().add(globalUserKey, userId.toString())
                                .then(reactiveRedisTemplate.opsForZSet().reverseRangeWithScores(key, Range.closed(0L, 0L))
                                        .next()
                                        .map(ZSetOperations.TypedTuple::getScore)
                                        .switchIfEmpty(Mono.just(0.0))
                                        .flatMap(highestScore -> {
                                            float newScore = (highestScore == 0.0) ? 1.0f : (float) (highestScore + 1);
                                            return reactiveRedisTemplate.opsForZSet().add(key, userId.toString(), newScore)
                                                    .then(Mono.just(ApiResponse.success(newScore)));
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
                        return Mono.just(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Store queue not found for storeId: " + storeId));
                    }
                });
    }

    public Mono<ApiResponse<String>> removeUserFromQueueForStore(UUID storeId, Long userId) {
        // TODO 권한 검증 추가

        String key = "queue:store:" + storeId + ":users";
        String globalUserKey = "queue:global:users";

        return reactiveRedisTemplate.opsForZSet().remove(key, userId.toString())
                .flatMap(result -> {
                    if (result > 0) {
                        return reactiveRedisTemplate.opsForSet().remove(globalUserKey, userId.toString())
                                .then(Mono.just(ApiResponse.<String>success("User removed from queue successfully")));
                    } else {
                        return Mono.just(ApiResponse.<String>error(HttpStatus.NOT_FOUND.value(), "User not found in queue"));
                    }
                })
                .onErrorResume(e -> Mono.just(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Failed to remove user from queue: " + e.getMessage())));
    }

    public Mono<ApiResponse<String>> resetQueueForStore(UUID storeId) {
        // TODO 권한 검증 추가

        String key = "queue:store:" + storeId + ":users";
        String globalUserKey = "queue:global:users";

        if (storeId == null) {
            return reactiveRedisTemplate.scan(ScanOptions.scanOptions().match("queue:store:*:users").build())
                    .flatMap(reactiveRedisTemplate::delete)
                    .then(reactiveRedisTemplate.delete(globalUserKey))
                    .then(Mono.just(ApiResponse.<String>success("All store queues have been reset successfully.")))
                    .onErrorResume(e -> {
                        log.error("Failed to reset all store queues", e);
                        return Mono.just(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "Failed to reset all store queues: " + e.getMessage()));
                    });
        } else {
            return reactiveRedisTemplate.opsForZSet().range(key, Range.closed(0L, -1L))
                    .filter(queuedUserId -> queuedUserId != null && !queuedUserId.isEmpty())
                    .flatMapSequential(queuedUserId -> {
                        if (queuedUserId != null && !queuedUserId.isEmpty()) {
                            return reactiveRedisTemplate.opsForSet().remove(globalUserKey, queuedUserId)
                                    .doOnSuccess(count -> log.debug("Removed {} from globalUserKey, result count: {}", queuedUserId, count))
                                    .onErrorResume(e -> {
                                        log.warn("Failed to remove {} from globalUserKey. Skipping.", queuedUserId, e);
                                        return Mono.empty();
                                    });
                        } else {
                            log.warn("Queued userId is null or empty, skipping: {}", queuedUserId);
                            return Mono.empty();
                        }
                    })
                    .then(reactiveRedisTemplate.delete(key))
                    .then(Mono.just(ApiResponse.<String>success("Store queue has been reset successfully.")))
                    .onErrorResume(e -> {
                        log.error("Failed to reset store queue for storeId: {}", storeId, e);
                        return Mono.just(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "Failed to reset store queue: " + e.getMessage()));
                    });
        }
    }
}