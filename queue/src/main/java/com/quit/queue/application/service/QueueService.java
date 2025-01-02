package com.quit.queue.application.service;

import com.quit.queue.application.service.dto.res.QueueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class QueueService {
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    // 대기열에 사용자 추가
    public Mono<Long> addUserToQueue(UUID storeId, Long userId) {
        String key = "queue:store:" + storeId + ":users";
        String globalUserKey = "queue:global:users"; // 모든 대기열에 존재하는 사용자 관리

        // 사용자가 이미 대기열에 있는지 글로벌 사용자 목록에서 확인
        return reactiveRedisTemplate.opsForSet().isMember(globalUserKey, userId.toString())
                .flatMap(isMember -> {
                    if (isMember) {
                        // 이미 다른 대기열에 추가되어 있는 사용자
                        return Mono.error(new IllegalStateException("User already in another queue"));
                    } else {
                        // 사용자가 다른 대기열에 없다면 글로벌 사용자 목록에 추가
                        return reactiveRedisTemplate.opsForSet().add(globalUserKey, userId.toString())
                                .then(reactiveRedisTemplate.opsForValue().increment("queue:store:" + storeId + ":counter", 1)
                                        .flatMap(newScore -> reactiveRedisTemplate.opsForZSet().add(key, userId.toString(), newScore)
                                                .thenReturn(newScore)));
                    }
                });
    }

    // store 별 대기열 조회
    public Flux<QueueResponse> getQueue(UUID storeId) {
        String key = "queue:store:" + storeId + ":users";

        // storeId가 존재하는지 확인
        return reactiveRedisTemplate.hasKey(key)
                .flatMapMany(exists -> {
                    if (exists) {
                        // storeId가 있을 경우 대기열 조회 후, 여러 사용자 정보를 QueueResponse에 추가
                        return reactiveRedisTemplate.opsForZSet().rangeWithScores(key, Range.closed(0L, -1L))
                                .collectList()  // 해당 storeId에 대한 모든 대기열을 리스트로 받음
                                .map(entries -> {
                                    QueueResponse response = new QueueResponse(storeId);
                                    // 각 entry를 QueueResponse의 userScores 리스트에 추가
                                    entries.forEach(entry ->
                                            response.addUserScore(Long.valueOf(entry.getValue()), entry.getScore().floatValue()));
                                    return response;
                                });
                    } else {
                        // storeId가 없으면 전체 대기열 조회 후 QueueResponse로 변환
                        return getAllQueues();
                    }
                });
    }

    // 전체 대기열 조회 (storeId 별로 그룹화된 결과 반환)
    private Flux<QueueResponse> getAllQueues() {
        // 전체 storeId를 조회하여 대기열을 반환
        return reactiveRedisTemplate.keys("queue:store:*:users")
                .flatMap(key -> reactiveRedisTemplate.opsForZSet().rangeWithScores(key, Range.closed(0L, -1L))
                        .collectList()  // 해당 storeId에 대한 모든 대기열을 리스트로 받음
                        .map(entries -> {
                            UUID storeId = UUID.fromString(key.split(":")[2]);
                            QueueResponse response = new QueueResponse(storeId);
                            // 각 entry를 QueueResponse의 userScores 리스트에 추가
                            entries.forEach(entry ->
                                    response.addUserScore(Long.valueOf(entry.getValue()), entry.getScore().floatValue()));
                            return response;
                        }));
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