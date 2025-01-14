package com.quit.store.infrastructure.client;

import com.quit.store.application.service.QueueClientService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "queue")
public interface QueueClient extends QueueClientService {

    @PostMapping("/api/queues/assign-store/{storeId}")
    void assignStoreToServer(@PathVariable UUID storeId, @RequestHeader(value = "X-User-Role") String userRole);
}
