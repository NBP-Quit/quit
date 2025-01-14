package com.quit.queue.application.service;

import com.quit.queue.common.ApiResponse;
import com.quit.queue.common.RoleValidationType;
import com.quit.queue.infrastructure.util.ServerInfo;
import com.quit.queue.presentation.exception.UnauthorizedException;
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
                .then(addStoreToServer(key, storeId))
                .then()
                .onErrorResume(e -> {
                    if (e instanceof UnauthorizedException) {
                        return Mono.error(new UnauthorizedException("Unauthorized role: " + userRole));
                    } else {
                        log.error("Failed to assign store {} to server {}: {}", storeId, serverId, e.getMessage());
                        return Mono.error(new RuntimeException("Failed to assign store to server"));
                    }
                })
                .doOnTerminate(() -> log.info("Store {} assigned to server {}", storeId, serverId));
    }

    public Mono<Void> assignStoreToSpecificServer(UUID storeId, String serverId) {
        String key = "queue:server:server-" + serverId + ":stores";

        return addStoreToServer(key, storeId)
                .then()
                .onErrorResume(e -> {
                    log.error("Failed to assign store {} to server {}: {}", storeId, serverId, e.getMessage());
                    return Mono.error(new RuntimeException("Failed to assign store to server"));
                })
                .doOnTerminate(() -> log.info("Store {} assigned to server {}", storeId, serverId));
    }

    public Mono<ApiResponse<Object>> changeStoreAssignment(UUID storeId, String fromServerId, String toServerId, String userRole) {
        return roleValidationService.validateUserRole(userRole, RoleValidationType.MASTER)
                .then(unassignStoreFromServer(storeId, fromServerId))
                .then(assignStoreToSpecificServer(storeId, toServerId))
                .then(Mono.just(ApiResponse.success("Store assignment changed successfully")))
                .onErrorResume(e -> {
                    if (e instanceof UnauthorizedException) {
                        return Mono.error(new UnauthorizedException("Unauthorized role: " + userRole));
                    } else {
                        log.error("Failed to change assignment for store {} from server {} to {}: {}", storeId, fromServerId, toServerId, e.getMessage());
                        return Mono.error(new RuntimeException("Failed to change store assignment to server"));
                    }
                })
                .doOnTerminate(() -> log.info("Store {} changed assignment from server {} to server {}", storeId, fromServerId, toServerId));
    }


    private Mono<Void> unassignStoreFromServer(UUID storeId, String serverId) {
        String key = "queue:server:server-" + serverId + ":stores";
        return reactiveRedisTemplate.opsForSet().remove(key, storeId.toString())
                .then();
    }

    private Mono<Void> addStoreToServer(String key, UUID storeId) {
        return reactiveRedisTemplate.opsForSet().add(key, storeId.toString())
                .then();
    }
}
