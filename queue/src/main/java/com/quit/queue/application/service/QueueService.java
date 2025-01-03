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
        String globalUserKey = "queue:global:users";

        return reactiveRedisTemplate.opsForSet().isMember(globalUserKey, userId.toString())
                .flatMap(isMember -> {
                    if (isMember) {
                        return Mono.just(ApiResponse.<Long>error(HttpStatus.BAD_REQUEST.value(), "User already in another queue"));
                    } else {
                        return reactiveRedisTemplate.opsForSet().add(globalUserKey, userId.toString())
                                .then(reactiveRedisTemplate.opsForValue().increment("queue:store:" + storeId + ":counter", 1)
                                        .flatMap(newScore -> reactiveRedisTemplate.opsForZSet().add(key, userId.toString(), newScore)
                                                .thenReturn(newScore)))
                                .flatMap(newScore -> Mono.just(ApiResponse.success(newScore)));
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

    private Mono<Void> removeUserFromQueue(UUID storeId, Long userId) {
        String key = "queue:store:" + storeId + ":users";
        String globalUserKey = "queue:global:users";

        return reactiveRedisTemplate.opsForZSet().remove(key, userId.toString())
                .then(reactiveRedisTemplate.opsForSet().remove(globalUserKey, userId.toString()))
                .then();
    }
}