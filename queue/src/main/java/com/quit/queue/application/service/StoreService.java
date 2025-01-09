package com.quit.queue.application.service;

import com.quit.queue.infrastructure.client.StoreClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreClient storeClient;

    public Mono<Boolean> getStoreForInternal(UUID storeId) {
        return storeClient.getStoreForInternal(storeId)
                .flatMap(apiResponse -> {
                    if (apiResponse.getData() != null && apiResponse.getData()) {
                        return Mono.just(true);
                    } else {
                        return Mono.error(new IllegalArgumentException("Invalid store ID or store not available for internal usage"));
                    }
                });
    }
}
