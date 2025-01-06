package com.quit.review.common;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

	private int code;
	private String message;
	private T data;

	public static <T> ApiResponse<T> success(HttpStatus httpStatus, String message) {
		return ApiResponse.<T>builder()
			.code(httpStatus.value())
			.message(message)
			.build();
	}

	public static <T> ApiResponse<T> success(T data) {
		return ApiResponse.<T>builder()
			.code(HttpStatus.OK.value())
			.data(data)
			.build();
	}

	public static <T> ApiResponse<T> error(HttpStatus httpStatus, String message) {
		return ApiResponse.<T>builder()
			.code(httpStatus.value())
			.message(message)
			.build();
	}

}