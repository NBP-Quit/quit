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
    public Mono<ApiResponse<?>> getQueue(@RequestParam(value = "storeId", required = false) UUID storeId) {
        return queueService.getQueue(storeId);
    }

    @PostMapping("/stores/{storeId}")
    public Mono<ApiResponse<?>> addUserToQueueForStore(@PathVariable UUID storeId,
                                                       @RequestBody ReservationRequest request,
                                                       @RequestHeader(value = "X-User-Id") String userId) {
        return queueService.addUserToQueueForStore(storeId, request, userId);
    }

    @DeleteMapping("/stores/{storeId}")
    public Mono<ApiResponse<Object>> removeUserFromQueueForStore(@PathVariable UUID storeId,
                                                                 @RequestHeader(value = "X-User-Id") String userId) {
        return queueService.removeUserFromQueueForStore(storeId, userId);
    }

    @GetMapping("/stores/{storeId}/users/position")
    public Mono<ApiResponse<Integer>> getUserPositionInQueueForStore(@PathVariable UUID storeId,
                                                                     @RequestHeader(value = "X-User-Id") String userId) {
        return queueService.getUserPositionInQueueForStore(storeId, userId);
    }

    @PostMapping("/stores/{storeId}/users/refresh")
    public Mono<ApiResponse<Integer>> checkUserInQueueForStore(@PathVariable UUID storeId,
                                                               @RequestHeader(value = "X-User-Id") String userId) {
        return queueService.checkUserInQueueForStore(storeId, userId);
    }

    @DeleteMapping("/reset")
    public Mono<ApiResponse<Object>> resetQueueForStore(@RequestParam(value = "storeId", required = false) UUID storeId,
                                                        @RequestHeader(value = "X-User-Id") Long userId) {
        return queueService.resetQueueForStore(storeId);
    }
}
