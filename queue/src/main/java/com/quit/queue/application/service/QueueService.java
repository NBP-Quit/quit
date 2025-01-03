package com.quit.queue.application.service;

import com.quit.queue.application.service.dto.res.QueueResponse;
import com.quit.queue.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class QueueService {
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    public Mono<ApiResponse<Long>> addUserToQueue(UUID storeId, Long userId) {
        // TODO 권한 검증 추가

        String key = "queue:store:" + storeId + ":users";
        String counterKey = "queue:store:" + storeId + ":counter";
        String globalUserKey = "queue:global:users";

        return reactiveRedisTemplate.opsForSet().isMember(globalUserKey, userId.toString())
                .flatMap(isMember -> {
                    if (isMember) {
                        return Mono.just(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "User already in another queue"));
                    } else {
                        return reactiveRedisTemplate.opsForSet().add(globalUserKey, userId.toString())
                                .then(reactiveRedisTemplate.opsForValue().increment(counterKey, 1))
                                .flatMap(newScore -> reactiveRedisTemplate.opsForZSet().add(key, userId.toString(), newScore)
                                        .then(Mono.just(ApiResponse.success(newScore))));
                    }
                });
    }

    public Mono<ApiResponse<List<QueueResponse>>> getQueue(UUID storeId) {
        // TODO 권한 검증 추가

        String key = "queue:store:" + storeId + ":users";

        return reactiveRedisTemplate.hasKey(key)
                .flatMap(exists -> {
                    if (exists) {
                        return reactiveRedisTemplate.opsForZSet().rangeWithScores(key, Range.closed(0L, -1L))
                                .map(entry -> {
                                    QueueResponse response = new QueueResponse(storeId);
                                    response.addUserScore(Long.valueOf(entry.getValue()), entry.getScore().floatValue());
                                    return response;
                                })
                                .collectList()
                                .map(ApiResponse::success);
                    } else {
                        return getAllQueues()
                                .map(ApiResponse::success);
                    }
                });
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

    public Mono<ApiResponse<String>> removeUserFromQueue(UUID storeId, Long userId) {
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
}