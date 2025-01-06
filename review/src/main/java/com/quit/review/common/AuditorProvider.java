package com.quit.review.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
public class AuditorProvider {

	@Getter
	private static AuditorAware<String> auditorAware;

	@Autowired
	public AuditorProvider(AuditorAwareImpl auditorAware) {
		AuditorProvider.auditorAware = auditorAware;
	}
}
