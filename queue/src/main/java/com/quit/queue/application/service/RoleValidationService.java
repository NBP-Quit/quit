package com.quit.queue.application.service;

import com.quit.queue.common.RoleValidationType;
import com.quit.queue.presentation.exception.UnauthorizedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RoleValidationService {

    public Mono<Void> validateUserRole(String userRole, int validationType) {
        return Mono.defer(() -> {
            switch (validationType) {
                case RoleValidationType.USER:
                    if (!userRole.equals("ROLE_USER")) {
                        return Mono.error(new UnauthorizedException("Unauthorized role: " + userRole));
                    }
                    break;
                case RoleValidationType.USER_OR_MASTER:
                    if (!(userRole.equals("ROLE_USER") || userRole.equals("ROLE_MASTER"))) {
                        return Mono.error(new UnauthorizedException("Unauthorized role: " + userRole));
                    }
                    break;
                case RoleValidationType.MASTER:
                    if (!userRole.equals("ROLE_MASTER")) {
                        return Mono.error(new UnauthorizedException("Unauthorized role: " + userRole));
                    }
                    break;
                case RoleValidationType.NOT_USER:
                    if (!(userRole.equals("ROLE_OWNER") || userRole.equals("ROLE_MASTER") || userRole.equals("ROLE_MANAGER"))) {
                        return Mono.error(new UnauthorizedException("Unauthorized role: " + userRole));
                    }
                    break;
                default:
                    return Mono.error(new IllegalArgumentException("Invalid validation type: " + validationType));
            }
            return Mono.empty();
        });
    }

    public void validateUserId(String requestUserId, String expectedUserId) {
        if (!requestUserId.equals(expectedUserId)) {
            throw new UnauthorizedException("Unauthorized userId: " + requestUserId);
        }
    }
}