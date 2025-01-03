package com.quit.queue.presentation.controller;

import com.quit.queue.application.service.QueueService;
import com.quit.queue.application.service.dto.res.QueueResponse;
import com.quit.queue.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/queues")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    @PostMapping("/stores/{storeId}")
    public Mono<ApiResponse<Long>> addUserToQueueForStore(@PathVariable UUID storeId,
                                                          @RequestHeader(value = "X-User-Id") Long userId) {
        return queueService.addUserToQueue(storeId, userId);
    }

    @GetMapping
    public Mono<ApiResponse<List<QueueResponse>>> getQueue(@RequestParam(value = "storeId", required = false) UUID storeId) {
        return queueService.getQueue(storeId);
    }
}
