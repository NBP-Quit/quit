package com.quit.queue.presentation.controller;

import com.quit.queue.application.service.QueueService;
import com.quit.queue.common.ApiResponse;
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
    public Mono<ApiResponse<Float>> addUserToQueueForStore(@PathVariable UUID storeId,
                                                           @RequestHeader(value = "X-User-Id") Long userId) {
        return queueService.addUserToQueueForStore(storeId, userId);
    }

    @DeleteMapping("/stores/{storeId}")
    public Mono<ApiResponse<String>> removeUserFromQueueForStore(@PathVariable UUID storeId,
                                                                 @RequestHeader(value = "X-User-Id") Long userId) {
        return queueService.removeUserFromQueueForStore(storeId, userId);
    }

    @GetMapping("/stores/{storeId}/users/position")
    public Mono<ApiResponse<Float>> getUserPositionInQueueForStore(@PathVariable UUID storeId,
                                                                   @RequestHeader(value = "X-User-Id") Long userId) {
        return queueService.getUserPositionInQueueForStore(storeId, userId);
    }

    @DeleteMapping("/reset")
    public Mono<ApiResponse<String>> resetQueueForStore(@RequestParam(value = "storeId", required = false) UUID storeId,
                                                        @RequestHeader(value = "X-User-Id") Long userId) {
        return queueService.resetQueueForStore(storeId);
    }
}
