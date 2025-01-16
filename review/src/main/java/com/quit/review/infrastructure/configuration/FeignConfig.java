package com.quit.review.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.quit.review.common.FeignClientInterceptor;

import feign.RequestInterceptor;

@Configuration
public class FeignConfig {

	@Bean
	public RequestInterceptor requestInterceptor() {
		return new FeignClientInterceptor();
	}
}
