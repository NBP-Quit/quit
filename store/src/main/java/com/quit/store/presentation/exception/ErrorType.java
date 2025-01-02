package com.quit.store.presentation.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorType {

    COMMON_INVALID_PARAMETER( BAD_REQUEST, "잘못된 파라미터입니다."),
    COMMON_SERVER_ERROR(INTERNAL_SERVER_ERROR, "서버에서 에러가 발생하였습니다."),
    COMMON_VALIDATION_ERROR(BAD_REQUEST, "요청 데이터가 유효하지 않습니다."),

    USER_NOT_SAME(BAD_REQUEST, "해당 작성자가 아닙니다."),

    STORE_NOT_FOUND(NOT_FOUND, "가게가 존재하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
