package com.quit.notification.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.slack.api.Slack;

@Configuration
public class SlackConfig {

	@Bean
	public Slack slack() {
		return Slack.getInstance();
	}
}
