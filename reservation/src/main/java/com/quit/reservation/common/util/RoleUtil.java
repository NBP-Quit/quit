package com.quit.reservation.common.util;

import org.springframework.stereotype.Component;

@Component
public class RoleUtil {
    public static String cleanRole(String role) {
        if (role != null && role.startsWith("ROLE_")) {
            return role.substring("ROLE_".length());
        }
        return role;
    }
}
