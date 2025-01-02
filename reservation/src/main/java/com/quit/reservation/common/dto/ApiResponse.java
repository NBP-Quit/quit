package com.quit.reservation.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int code;
    private String statusMessage;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .code(HttpStatus.OK.value())
                .statusMessage(HttpStatus.OK.getReasonPhrase())
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(HttpStatus.OK.value())
                .statusMessage(HttpStatus.OK.getReasonPhrase())
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .statusMessage(HttpStatus.OK.getReasonPhrase())
                .message(message)
                .build();
    }
}
