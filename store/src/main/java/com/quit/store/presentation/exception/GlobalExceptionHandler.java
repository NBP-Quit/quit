package com.quit.store.presentation.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.boot.web.servlet.server.Encoding.DEFAULT_CHARSET;
import static org.springframework.http.HttpHeaders.CONTENT_ENCODING;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final int FIRST_ERROR_INDEX = 0;

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomErrorResponse> handleCustomException(CustomException e) {
        logError(e);
        ErrorType errorType = e.getErrorType();
        return createResponseEntity(errorType);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logError(e);
        String firstErrorMessage = getFirstErrorMessage(e);
        ErrorType errorType = errorMessageToErrorCode(firstErrorMessage, ErrorType.COMMON_INVALID_PARAMETER);
        return createResponseEntity(errorType);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CustomErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        logError(e);
        return createResponseEntity(BAD_REQUEST, null, e.getMessage());
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<CustomErrorResponse> handleNumberFormatException(NumberFormatException e) {
        logError(e);
        return createResponseEntity(BAD_REQUEST, null, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleException(Exception e) {
        logError(e);
        if (e.getCause() instanceof CustomException customException) {
            return createResponseEntity(customException.getErrorType());
        }
        return createResponseEntity(INTERNAL_SERVER_ERROR, ErrorType.COMMON_SERVER_ERROR.name(), e.getMessage());
    }

    private ErrorType errorMessageToErrorCode(String errorMessage, ErrorType defaultErrorCode) {
        try {
            return ErrorType.valueOf(errorMessage);
        } catch (IllegalArgumentException | NullPointerException ex) {
            return defaultErrorCode;
        }

    }

    private String getFirstErrorMessage(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        return bindingResult
                .getAllErrors()
                //첫 번째 에러만 반환
                .get(FIRST_ERROR_INDEX)
                .getDefaultMessage();
    }

    private ResponseEntity<CustomErrorResponse> createResponseEntity(ErrorType errorType) {
        return createResponseEntity(errorType.getHttpStatus(), errorType.name(), errorType.getMessage());
    }

    private ResponseEntity<CustomErrorResponse> createResponseEntity(HttpStatus httpStatus, String code, String message) {
        CustomErrorResponse customErrorResponse = CustomErrorResponse.builder()
                .code(code)
                .message(message)
                .build();
        return ResponseEntity
                .status(httpStatus)
                .header(CONTENT_ENCODING, DEFAULT_CHARSET.name())
                .contentType(APPLICATION_JSON)
                .body(customErrorResponse);
    }

    private void logError(Exception e) {
        log.error(e.getClass().getSimpleName(), e);
    }

}
