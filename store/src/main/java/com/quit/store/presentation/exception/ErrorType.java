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
    STORE_NAME_EMPTY(BAD_REQUEST, "가게 이름이 존재하지 않습니다."),
    STORE_ADDRESS_EMPTY(BAD_REQUEST, "가게 주소가 존재하지 않습니다."),
    STORE_CONTACT_NUMBER_EMPTY(BAD_REQUEST, "가게 연락처가 존재하지 않습니다."),
    STORE_RESERVATION_DEPOSIT_EMPTY(BAD_REQUEST, "가게 예약금이 존재하지 않습니다."),
    STORE_RESERVATION_DEPOSIT_INVALID(BAD_REQUEST, "가게 예약금이 유효하지 않습니다."),
    STORE_OPEN_TIME_EMPTY(BAD_REQUEST, "가게 오픈 시간이 존재하지 않습니다."),
    STORE_CLOSE_TIME_EMPTY(BAD_REQUEST, "가게 마감 시간이 존재하지 않습니다."),
    STORE_LAST_ORDER_TIME_EMPTY(BAD_REQUEST, "가게 주문 마감 시간이 존재하지 않습니다."),
    STORE_CATEGORY_EMPTY(BAD_REQUEST, "가게 카테고리가 존재하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
