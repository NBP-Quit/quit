package com.quit.gateway.infrastructure.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitConfig {

    @Bean
    KeyResolver userKeyResolver() {
        return exchange -> {
            // 로그인 URL일 때만 IP 주소를 키 값으로 사용
            if (exchange.getRequest().getPath().value().equals("/api/auth/login")) {
                String ipAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
                return Mono.just(ipAddress);
            }
            return Mono.just("default"); // 기본 키 값 설정 (필요한 경우)
        };
    }
}

