package com.quit.queue.application.service;

import com.quit.queue.common.RoleValidationType;
import com.quit.queue.infrastructure.util.ServerInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreServerAssignmentService {
    private final RoleValidationService roleValidationService;
    private final ServerInfo serverInfo;

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    public Mono<Void> assignStoreToServer(UUID storeId, String userRole) {
        String serverId = serverInfo.getServerId();
        String key = "queue:server:" + serverId + ":stores";

        return roleValidationService.validateUserRole(userRole, RoleValidationType.NOT_USER)
                .then(
                        reactiveRedisTemplate.opsForSet().add(key, storeId.toString())
                )
                .then()
                .onErrorResume(e -> {
                    log.error("Failed to assign store {} to server {}: {}", storeId, serverId, e.getMessage());
                    return Mono.error(new RuntimeException("Failed to assign store to server"));
                })
                .doOnTerminate(() -> log.info("Store {} assigned to server {}", storeId, serverId));
    }
}
