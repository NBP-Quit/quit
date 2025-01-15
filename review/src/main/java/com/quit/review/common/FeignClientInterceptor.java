package com.quit.review.common;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;

public class FeignClientInterceptor implements RequestInterceptor {
	@Override
	public void apply(RequestTemplate requestTemplate) {
		ServletRequestAttributes attributes =
			(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

		if (attributes != null) {
			HttpServletRequest request = attributes.getRequest();

			// 복사할 헤더 키들을 지정하거나 모든 헤더를 복사
			request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
				String headerValue = request.getHeader(headerName);
				if (headerValue != null) {
					requestTemplate.header(headerName, headerValue);
				}
			});
		}
	}
}
