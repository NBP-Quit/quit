package com.quit.review.common;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {
	@Override
	public Optional<String> getCurrentAuditor() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attributes != null) {
			HttpServletRequest request = attributes.getRequest();
			String userId = request.getHeader("X-User-Id");
			if (userId != null && !userId.isEmpty()) {
				return Optional.of(userId);
			}
		}
		return Optional.of("system");
	}
}
