package com.quit.review.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomApiException.class)
	public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomApiException ex) {
		return ResponseEntity.status(ex.getHttpStatus()).body(ApiResponse.error(ex.getHttpStatus(), ex.getMessage()));
	}
}
