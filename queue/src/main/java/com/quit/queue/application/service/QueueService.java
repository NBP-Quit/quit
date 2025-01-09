package com.quit.queue.application.service;

import com.quit.queue.application.dto.ReservationDto;
import com.quit.queue.application.dto.res.QueueResponse;
import com.quit.queue.common.ApiResponse;
import com.quit.queue.presentation.request.ReservationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    private final RoleValidationService roleValidationService;

    public Mono<ApiResponse<?>> addUserToQueueForStore(UUID storeId, ReservationRequest reservationRequest, String userId, String userRole) {
        roleValidationService.validateUserRole(userRole, 2);

        return validateReservationRequest(reservationRequest)
                .switchIfEmpty(Mono.defer(() -> {
                    String key = "queue:store:" + storeId + ":users";
                    String reservationKey = "queue:store:" + storeId + ":reservations:" + userId;
                    String refreshKey = "queue:store:" + storeId + ":refresh:" + userId;
                    String userQueueKey = "queue:user:" + userId;

                    ReservationDto reservationDto = reservationRequest.toDTO();

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
                                                        double newScore = (highestScore == 0.0) ? 1.0 : highestScore + 1;
                                                        return reactiveRedisTemplate.opsForZSet().add(key, userId, newScore)
                                                                .then(reactiveRedisTemplate.opsForHash().putAll(reservationKey, Map.of(
                                                                        "guestCount", reservationDto.getGuestCount().toString(),
                                                                        "reservationDate", reservationDto.getReservationDate().toString(),
                                                                        "reservationTime", reservationDto.getReservationTime().toString()
                                                                )))
                                                                .then(reactiveRedisTemplate.opsForValue().set(refreshKey, String.valueOf(System.currentTimeMillis())))
                                                                .then(reactiveRedisTemplate.expire(refreshKey, Duration.ofMinutes(5)))
                                                                .then(reactiveRedisTemplate.opsForZSet().rank(key, userId))
                                                                .flatMap(rank -> {
                                                                    if (rank == null) {
                                                                        return Mono.error(new IllegalStateException("Failed to get rank"));
                                                                    }
                                                                    return Mono.just(ApiResponse.success(rank + 1));
                                                                });
                                                    }));
                                }
                            });
                }));
    }

    private Mono<ApiResponse<?>> validateReservationRequest(ReservationRequest reservationRequest) {
        if (reservationRequest.getGuestCount() < 0) {
            return Mono.error(new IllegalArgumentException("Guest count must be at least 1"));
        }

        if (reservationRequest.getReservationDate() == null || reservationRequest.getReservationTime() == null) {
            return Mono.error(new IllegalArgumentException("Reservation date and time must not be null"));
        }

        return Mono.empty();
    }

    public Mono<ApiResponse<Integer>> getUserPositionInQueueForStore(UUID storeId, String userId, String userRole) {
        roleValidationService.validateUserRole(userRole, 2);

        String key = "queue:store:" + storeId + ":users";

        return reactiveRedisTemplate.opsForZSet().rank(key, userId)
                .switchIfEmpty(Mono.just(-1L))
                .flatMap(rank -> {
                    if (rank == null || rank == -1) {
                        return Mono.error(new IllegalStateException("User not found in queue"));
                    }
                    return Mono.just(ApiResponse.success(rank.intValue()));
                });
    }

    public Mono<ApiResponse<?>> getQueue(UUID storeId, String userRole) {
        roleValidationService.validateUserRole(userRole, 3);

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
                                        response.addUserScore(Long.valueOf(entry.getValue()), entry.getScore().intValue()));
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
                                            response.addUserScore(Long.valueOf(entry.getValue()), entry.getScore().intValue()));
                                    return response;
                                })
                                .map(ApiResponse::success);
                    } else {
                        return Mono.error(new IllegalArgumentException("Store queue not found for storeId: " + storeId));
                    }
                });
    }

    public Mono<ApiResponse<Object>> removeUserFromQueueForStore(UUID storeId, String paramUserId, String userId, String userRole) {
        roleValidationService.validateUserRole(userRole, 2);

        final String finalUserId;
        if (userRole.equals("ROLE_MASTER")) {
            finalUserId = paramUserId;
        } else {
            finalUserId = userId;
        }

        String key = "queue:store:" + storeId + ":users";
        String userQueueKey = "queue:user:" + finalUserId;
        String refreshKey = "queue:store:" + storeId + ":refresh:" + finalUserId;

        return reactiveRedisTemplate.opsForValue().get(userQueueKey)
                .flatMap(currentQueue -> {
                    if (currentQueue != null && !currentQueue.equals(storeId.toString())) {
                        return Mono.error(new IllegalStateException("User not in the specified store queue"));
                    }

                    return reactiveRedisTemplate.opsForZSet().remove(key, finalUserId)
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

    public Mono<ApiResponse<Object>> resetQueueForStore(UUID storeId, String userRole) {
        roleValidationService.validateUserRole(userRole, 3);

        return (storeId == null) ? resetAllQueues() : resetStoreQueue(storeId);
    }

    private Mono<ApiResponse<Object>> resetAllQueues() {
        return reactiveRedisTemplate.scan(ScanOptions.scanOptions().match("queue:*").count(1000).build())
                .buffer(100)
                .flatMap(keys -> {
                    if (!keys.isEmpty()) {
                        return reactiveRedisTemplate.delete(Flux.fromIterable(keys).limitRate(10))
                                .then();
                    }
                    return Mono.empty();
                })
                .then(Mono.just(ApiResponse.success("All queues have been reset successfully.")))
                .onErrorMap(e -> new RuntimeException("Failed to reset all queues", e));
    }

    private Mono<ApiResponse<Object>> resetStoreQueue(UUID storeId) {
        String key = "queue:store:" + storeId + ":users";
        String patternKey = "queue:store:" + storeId + ":*";

        return reactiveRedisTemplate.opsForZSet().range(key, Range.closed(0L, -1L))
                .filter(queuedUserId -> queuedUserId != null && !queuedUserId.isEmpty())
                .flatMapSequential(this::removeFromUserQueue)
                .then(deleteKeysWithPattern(patternKey))
                .then(Mono.just(ApiResponse.success("Store queue has been reset successfully.")))
                .onErrorMap(e -> new RuntimeException("Failed to reset store queue for storeId: " + storeId, e));
    }

    private Mono<Void> removeFromUserQueue(String queuedUserId) {
        String userQueueKey = "queue:user:" + queuedUserId;

        return reactiveRedisTemplate.delete(userQueueKey)
                .doOnSuccess(count -> log.debug("Removed user {} and its refresh key from queue info", queuedUserId))
                .onErrorMap(e -> new RuntimeException("Failed to remove " + queuedUserId + " from its queue info.", e))
                .then();
    }

    private Mono<Void> deleteKeysWithPattern(String patternKey) {
        return reactiveRedisTemplate.scan(ScanOptions.scanOptions().match(patternKey).count(1000).build())
                .buffer(100)
                .flatMap(keys -> {
                    if (!keys.isEmpty()) {
                        return reactiveRedisTemplate.delete(Flux.fromIterable(keys).limitRate(10))
                                .then();
                    }
                    return Mono.empty();
                })
                .then()
                .onErrorMap(e -> new RuntimeException("Failed to delete keys with pattern: " + patternKey, e));
    }

    public Mono<ApiResponse<Integer>> checkUserInQueueForStore(UUID storeId, String userId, String userRole) {
        roleValidationService.validateUserRole(userRole, 1);

        String queueKey = "queue:store:" + storeId + ":users";
        String refreshKey = "queue:store:" + storeId + ":refresh:" + userId;

        return reactiveRedisTemplate.opsForZSet().rank(queueKey, userId)
                .switchIfEmpty(Mono.just(-1L))
                .flatMap(rank -> {
                    if (rank == null || rank == -1) {
                        return Mono.error(new IllegalStateException("User not found in queue"));
                    }

                    return reactiveRedisTemplate.opsForValue().set(refreshKey, String.valueOf(System.currentTimeMillis()))
                            .then(reactiveRedisTemplate.expire(refreshKey, Duration.ofMinutes(5)))
                            .then(Mono.just(ApiResponse.success(rank.intValue() + 1)));
                });
    }
}