package com.quit.queue.presentation.controller;

import com.quit.queue.application.service.QueueService;
import com.quit.queue.common.ApiResponse;
import com.quit.queue.presentation.request.ReservationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/queues")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    @GetMapping
    public Mono<ApiResponse<?>> getQueue(@RequestParam(value = "storeId", required = false) UUID storeId,
                                         @RequestHeader(value = "X-User-Role") String userRole) {
        return queueService.getQueue(storeId, userRole);
    }

    @PostMapping("/stores/{storeId}")
    public Mono<ApiResponse<?>> addUserToQueueForStore(@PathVariable UUID storeId,
                                                       @RequestBody ReservationRequest request,
                                                       @RequestHeader(value = "X-User-Id") String userId,
                                                       @RequestHeader(value = "X-User-Email") String userEmail,
                                                       @RequestHeader(value = "X-User-Role") String userRole) {
        return queueService.addUserToQueueForStore(storeId, request, userId, userEmail, userRole);
    }

    @DeleteMapping("/stores/{storeId}")
    public Mono<ApiResponse<Object>> removeUserFromQueueForStore(@PathVariable UUID storeId,
                                                                 @RequestParam(value = "userId", required = false) String paramUserId,
                                                                 @RequestHeader(value = "X-User-Id") String userId,
                                                                 @RequestHeader(value = "X-User-Role") String userRole) {
        return queueService.removeUserFromQueueForStore(storeId, paramUserId, userId, userRole);
    }

    @GetMapping("/stores/{storeId}/users/position")
    public Mono<ApiResponse<Integer>> getUserPositionInQueueForStore(@PathVariable UUID storeId,
                                                                     @RequestHeader(value = "X-User-Id") String userId,
                                                                     @RequestHeader(value = "X-User-Role") String userRole) {
        return queueService.getUserPositionInQueueForStore(storeId, userId, userRole);
    }

    @PostMapping("/stores/{storeId}/users/refresh")
    public Mono<ApiResponse<Integer>> checkUserInQueueForStore(@PathVariable UUID storeId,
                                                               @RequestHeader(value = "X-User-Id") String userId,
                                                               @RequestHeader(value = "X-User-Role") String userRole) {
        return queueService.checkUserInQueueForStore(storeId, userId, userRole);
    }

    @DeleteMapping("/reset")
    public Mono<ApiResponse<Object>> resetQueueForStore(@RequestParam(value = "storeId", required = false) UUID storeId,
                                                        @RequestHeader(value = "X-User-Role") String userRole) {
        return queueService.resetQueueForStore(storeId, userRole);
    }
}
