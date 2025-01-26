package com.quit.user.common.security;

import com.quit.user.common.filter.JwtAuthenticationFilter;
import com.quit.user.common.filter.JwtAuthorizationFilter;
import com.quit.user.common.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;
    private final AuthenticationConfiguration authenticationConfiguration;

    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final RedisTemplate redisTemplate;


    public WebSecurityConfig(JwtUtil jwtUtil, AuthenticationConfiguration authenticationConfiguration, JwtAuthorizationFilter jwtAuthorizationFilter, RedisTemplate redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
        this.redisTemplate = redisTemplate;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //csrf 보호 비활성화
        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests((authorizationRequests) -> authorizationRequests
                        .requestMatchers("/api/auth/login", "/api/auth/signup").permitAll() // 회원가입, 로그인 등에 대한 요청 접근 허용
                .anyRequest().authenticated() )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterAt(new JwtAuthenticationFilter(authenticationManager(authenticationConfiguration),jwtUtil), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        http.logout(logout -> logout
                .logoutUrl("/api/auth/logout") // 로그아웃 엔드포인트 설정
                .logoutSuccessHandler((request, response, authentication) -> {
                    // 헤더에서 JWT 추출
                    String token = request.getHeader("Authorization");
                    if (token != null && token.startsWith("Bearer ")) {
                        token = token.substring(7); // "Bearer " 부분 제거
                        // 토큰을 블랙리스트에 저장 (예: Redis 사용)
                        long expiration = jwtUtil.getExpiration(token);
                        redisTemplate.opsForValue().set("blacklist:" + token, true, expiration, TimeUnit.MILLISECONDS);
                    }

                    // 성공 메시지 반환
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\": \"Successfully logged out\"}");
                    response.getWriter().flush();
                })
                .invalidateHttpSession(true) // 세션 무효화 (JWT에서는 큰 의미 없음)
                .clearAuthentication(true) // SecurityContext 초기화
        );


        return http.build();

    }
}
