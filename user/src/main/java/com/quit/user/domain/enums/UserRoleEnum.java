package com.quit.user.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRoleEnum {
    USER,  // 일반 사용자
    OWNER,  // 가게 주인
    STORE_MANAGER,  // 가게 담당 관리자
    MASTER ;  // 전체 관리자

    // ROLE_프리픽스를 제거한 권한
    public static UserRoleEnum fromRole(String role) {
        // "ROLE_"을 제거하고 Enum 값으로 매핑
        if (role != null && role.startsWith("ROLE_")) {
            role = role.substring(5); // "ROLE_"을 제거
        }

        try {
            // Enum 값으로 매핑
            return UserRoleEnum.valueOf(role);
        } catch (IllegalArgumentException e) {
            // 매칭되는 값이 없을 경우 예외 처리
            throw new IllegalArgumentException("Unknown role: " + role);
        }
    }
}
