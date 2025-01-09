package com.quit.gateway.infrastructure.config;

import com.quit.gateway.common.jwt.JwtUtil;
import com.quit.gateway.dto.CustomHeader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig implements GlobalFilter {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        System.out.println(path);
        if (path.startsWith("/api/auth")) {
            return chain.filter(exchange);
        }

        // 토큰 추출
        final String token = jwtUtil.extractToken(exchange.getRequest());

        // JWT 유효성 검사
        if (!jwtUtil.validateToken(token)) {
            // 토큰이 존재하지 않거나 유요하지 않을 경우 예외처리
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        //payload 추출
        CustomHeader customHeaderData = jwtUtil.extractUserDetails(token);

        ServerWebExchange customExchange = jwtUtil.setCustomHeader(exchange, customHeaderData);

        return chain.filter(customExchange);
    }
}