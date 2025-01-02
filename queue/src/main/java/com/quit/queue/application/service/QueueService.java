package com.quit.queue.application.service;

import com.quit.queue.application.service.dto.res.QueueResponse;
import com.quit.queue.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class QueueService {
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    // 대기열에 사용자 추가
    public Mono<ApiResponse<Long>> addUserToQueue(UUID storeId, Long userId) {
        String key = "queue:store:" + storeId + ":users";
        String globalUserKey = "queue:global:users"; // 모든 대기열에 존재하는 사용자 관리

        // 사용자가 이미 대기열에 있는지 글로벌 사용자 목록에서 확인
        return reactiveRedisTemplate.opsForSet().isMember(globalUserKey, userId.toString())
                .flatMap(isMember -> {
                    if (isMember) {
                        // 이미 다른 대기열에 추가되어 있는 사용자
                        return Mono.just(ApiResponse.<Long>error(HttpStatus.BAD_REQUEST.value(), "User already in another queue"));
                    } else {
                        // 사용자가 다른 대기열에 없다면 글로벌 사용자 목록에 추가
                        return reactiveRedisTemplate.opsForSet().add(globalUserKey, userId.toString())
                                .then(reactiveRedisTemplate.opsForValue().increment("queue:store:" + storeId + ":counter", 1)
                                        .flatMap(newScore -> reactiveRedisTemplate.opsForZSet().add(key, userId.toString(), newScore)
                                                .thenReturn(newScore)))
                                .flatMap(newScore -> Mono.just(ApiResponse.success(newScore)));
                    }
                });
    }

    public Mono<ApiResponse<List<QueueResponse>>> getQueue(UUID storeId) {
        String key = "queue:store:" + storeId + ":users";

        return reactiveRedisTemplate.hasKey(key)
                .flatMap(exists -> {
                    if (exists) {
                        // storeId가 있을 경우 대기열 조회 후, 여러 사용자 정보를 QueueResponse에 추가
                        return reactiveRedisTemplate.opsForZSet().rangeWithScores(key, Range.closed(0L, -1L))
                                .map(entry -> {
                                    QueueResponse response = new QueueResponse(storeId);
                                    response.addUserScore(Long.valueOf(entry.getValue()), entry.getScore().floatValue());
                                    return response;
                                })
                                .collectList()
                                .map(ApiResponse::success);
                    } else {
                        // storeId가 없으면 전체 대기열 조회 후 QueueResponse로 변환
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

    // 대기열에서 사용자 제거
    private Mono<Void> removeUserFromQueue(UUID storeId, Long userId) {
        String key = "queue:store:" + storeId + ":users";
        String globalUserKey = "queue:global:users";

        return reactiveRedisTemplate.opsForZSet().remove(key, userId.toString())
                .then(reactiveRedisTemplate.opsForSet().remove(globalUserKey, userId.toString()))
                .then();
    }
}