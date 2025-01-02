package com.quit.queue.presentation.controller;

import com.quit.queue.application.service.QueueService;
import com.quit.queue.application.service.dto.res.QueueResponse;
import com.quit.queue.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/queues")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    @PostMapping("/{storeId}/users")
    public Mono<ApiResponse<Long>> addUserToQueueForStore(@PathVariable UUID storeId,
                                                    @RequestHeader(value = "X-User-Id", required = true) Long userId) {
        // 비동기적으로 사용자 대기열에 추가
        return queueService.addUserToQueue(storeId, userId);
    }

    @GetMapping
    public  Mono<ApiResponse<List<QueueResponse>>> getQueue(@RequestParam(value = "storeId", required = false) UUID storeId) {
        // 비동기적으로 대기열 데이터를 가져오기
        return queueService.getQueue(storeId);
    }
}
