package com.quit.review.common;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CustomApiException extends RuntimeException{
	private HttpStatus httpStatus;

	public CustomApiException(HttpStatus httpstatus, String message) {
		super(message);
		this.httpStatus = httpstatus;
	}
}
