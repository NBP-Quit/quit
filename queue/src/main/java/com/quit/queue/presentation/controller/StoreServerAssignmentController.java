package com.quit.queue.presentation.controller;

import com.quit.queue.application.service.StoreServerAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/queues/assign-store")
@RequiredArgsConstructor
public class StoreServerAssignmentController {
    private final StoreServerAssignmentService storeServerAssignmentService;

    @PostMapping("{storeId}")
    public Mono<Void> assignStoreToServer(@PathVariable UUID storeId,
                                          @RequestHeader(value = "X-User-Role") String userRole) {
        return storeServerAssignmentService.assignStoreToServer(storeId, userRole);
    }
}
