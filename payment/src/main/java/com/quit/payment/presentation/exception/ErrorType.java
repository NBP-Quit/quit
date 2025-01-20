package com.quit.payment.presentation.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorType {

    COMMON_INVALID_PARAMETER(BAD_REQUEST, "잘못된 파라미터입니다."),
    COMMON_SERVER_ERROR(INTERNAL_SERVER_ERROR, "서버에서 에러가 발생하였습니다."),
    COMMON_VALIDATION_ERROR(BAD_REQUEST, "요청 데이터가 유효하지 않습니다."),

    PAYMENT_DATA_INVALID(NOT_FOUND, "해당 결제 데이터가 존재하지 않습니다."),
    PAYMENT_NOT_FOUND(NOT_FOUND, "결제가 존재하지 않습니다."),
    PAYMENT_CANCEL_REASON_EMPTY(BAD_REQUEST, "결제 취소 사유가 존재하지 않습니다."),
    PAYMENT_ORDER_ID_EMPTY(BAD_REQUEST, "결제 외주사 주문 ID가 존재하지 않습니다."),
    PAYMENT_KEY_EMPTY(BAD_REQUEST, "결제 외주사 결제키가 존재하지 않습니다."),
    PAYMENT_AMOUNT_EMPTY(BAD_REQUEST, "결제 금액이 존재하지 않습니다."),
    PAYMENT_AMOUNT_INVALID(BAD_REQUEST, "결제 금액이 유효하지 않습니다."),
    PAYMENT_IDEMPOTENCY_KEY_EMPTY(BAD_REQUEST, "멱등키가 존재하지 않습니다."),

    RESERVATION_ID_MISMATCH(BAD_REQUEST, "결제 데이터와 예약 ID가 일치하지 않습니다."),

    KAFKA_MESSAGE_SEND_FAILED(INTERNAL_SERVER_ERROR, "Kafka 메시지 발행에 실패하였습니다."),

    FEIGN_CLIENT_INVALID_REQUEST(BAD_REQUEST, "FeignClient 요청에서 잘못된 요청이 발생했습니다."),
    FEIGN_CLIENT_RESOURCE_NOT_FOUND(NOT_FOUND, "FeignClient 요청에서 리소스를 찾을 수 없습니다."),
    FEIGN_CLIENT_UNKNOWN_ERROR(INTERNAL_SERVER_ERROR, "FeignClient 요청 중 알 수 없는 에러가 발생했습니다."),

    REQUEST_ALREADY_PROCESSED(CONFLICT, "이미 처리된 요청입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
