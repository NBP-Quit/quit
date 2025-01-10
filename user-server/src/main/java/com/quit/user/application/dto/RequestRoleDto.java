package com.quit.user.application.dto;

import com.quit.user.domain.enums.RequestStatus;
import com.quit.user.domain.enums.UserRoleEnum;
import com.quit.user.domain.model.RequestRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record RequestRoleDto(
        UUID id,
        Long userId,
        UserRoleEnum requestRole,
        LocalDateTime requestedAt,
        RequestStatus status,
        LocalDateTime approvedAt) {
    public static RequestRoleDto of(final RequestRole role) {
        return new RequestRoleDto(
                role.getId(),
                role.getUserId(),
                role.getRequestRole(),
                role.getRequestedAt(),
                role.getStatus(),
                role.getApprovedAt()
        );
    }
}
