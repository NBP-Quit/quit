package com.quit.user.presentation.request;

import com.quit.user.domain.enums.UserRoleEnum;

public record RoleRequest(
        UserRoleEnum requestRole
) {

}
