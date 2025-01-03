package com.quit.user.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRoleEnum {
    USER,  // 일반 사용자
    OWNER,  // 가게 주인
    STORE_MANAGER,  // 가게 담당 관리자
    MASTER   // 전체 관리자

}
