package com.quit.user.application.dto;

import com.quit.user.domain.enums.UserRoleEnum;
import com.quit.user.domain.model.User;

public record UserDto(
        Long id,
        String email,
        String nickname,
        UserRoleEnum role,
        String phone,
        String birthdate,
        String address
) {

    public static UserDto of(final User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getRole(),
                user.getPhone(),
                user.getBirthdate(),
                user.getAddress()
        );
    }
}
