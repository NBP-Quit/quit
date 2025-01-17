package com.quit.user.infrastructure.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class PrometheusAuthorizationFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (path.equals("/actuator/prometheus")) {
            String method = exchange.getRequest().getMethod().name();
            if (!method.equals("GET")) {
                return chain.filter(exchange);
            }

            String userAgent = exchange.getRequest().getHeaders().getFirst("User-Agent");
            if (userAgent != null && userAgent.contains("Prometheus")) {
                return chain.filter(exchange);
            }

            String headerValue = exchange.getRequest().getHeaders().getFirst("X-User-Role");
            if (headerValue == null || !headerValue.equals("ROLE_MASTER")) {
                return Mono.error(new RuntimeException("Unauthorized"));
            }
        }

        return chain.filter(exchange);
    }
}
