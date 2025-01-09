package com.quit.queue.application.service;

import com.quit.queue.common.RoleValidationType;
import com.quit.queue.presentation.exception.UnauthorizedException;
import org.springframework.stereotype.Service;

@Service
public class RoleValidationService {

    public void validateUserRole(String userRole, int validationType) {
        switch (validationType) {
            case RoleValidationType.USER:
                if (!userRole.equals("ROLE_USER")) {
                    throw new UnauthorizedException("Unauthorized role: " + userRole);
                }
                break;
            case RoleValidationType.USER_OR_MASTER:
                if (!(userRole.equals("ROLE_USER") || userRole.equals("ROLE_MASTER"))) {
                    throw new UnauthorizedException("Unauthorized role: " + userRole);
                }
                break;
            case RoleValidationType.MASTER:
                if (!userRole.equals("ROLE_MASTER")) {
                    throw new UnauthorizedException("Unauthorized role: " + userRole);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid validation type: " + validationType);
        }
    }

    public void validateUserId(String requestUserId, String expectedUserId) {
        if (!requestUserId.equals(expectedUserId)) {
            throw new UnauthorizedException("Unauthorized userId: " + requestUserId);
        }
    }
}