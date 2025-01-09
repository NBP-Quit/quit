package com.quit.store.common.util;

import com.quit.store.presentation.exception.CustomException;
import org.springframework.stereotype.Component;

import static com.quit.store.presentation.exception.ErrorType.COMMON_VALIDATION_ERROR;
import static com.quit.store.presentation.exception.ErrorType.USER_NOT_AUTHORIZED;

@Component
public class RoleValidator {

    public void validateRole(String userRole, Action action) {
        switch (action) {
            case CREATE:
            case UPDATE:
            case SLOT_DELETE:
                if (!(userRole.equals("ROLE_OWNER") || userRole.equals("ROLE_STORE_MANAGER") || userRole.equals("ROLE_MASTER"))) {
                    throw new CustomException(USER_NOT_AUTHORIZED);
                }
                break;
            case STORE_DELETE:
                if (!(userRole.equals("ROLE_STORE_MANAGER") || userRole.equals("ROLE_MASTER"))) {
                    throw new CustomException(USER_NOT_AUTHORIZED);
                }
                break;
            default:
                throw new CustomException(COMMON_VALIDATION_ERROR);
        }
    }

    public enum Action {
        CREATE,
        UPDATE,
        STORE_DELETE,
        SLOT_DELETE,
    }

}
