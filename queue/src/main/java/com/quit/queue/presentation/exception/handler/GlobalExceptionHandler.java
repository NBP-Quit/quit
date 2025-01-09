package com.quit.queue.presentation.exception.handler;

import com.quit.queue.common.ApiResponse;
import com.quit.queue.presentation.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Mono<ApiResponse<?>> handleException(Exception ex) {
        log.error("Unexpected error occurred: ", ex);
        return Mono.just(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred."));
    }

    @ExceptionHandler(IllegalStateException.class)
    public Mono<ApiResponse<?>> handleIllegalStateException(IllegalStateException ex) {
        log.warn("Illegal state exception: ", ex);
        return Mono.just(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Invalid argument: ", ex);
        return Mono.just(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public Mono<ApiResponse<?>> handleRuntimeException(RuntimeException ex) {
        log.warn("Runtime exception: ", ex);
        return Mono.just(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public Mono<ApiResponse<?>> handleUnauthorizedException(UnauthorizedException ex) {
        log.warn("UnauthorizedException exception: ", ex);
        return Mono.just(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }
}