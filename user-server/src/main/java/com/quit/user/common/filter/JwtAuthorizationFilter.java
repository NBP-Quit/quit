package com.quit.user.common.filter;

import com.quit.user.common.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {


        // 인증(auth) 관련 요청은 토큰 X
        if (request.getRequestURI().startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("request : {}",request.getHeader("X-User-Id"));
        String userId = request.getHeader("X-User-Id");
        String email = request.getHeader("X-User-Email");
        String userRole = request.getHeader("X-User-Role");

        if (userId != null && userRole != null) {
            try {
                jwtUtil.setAuthentication(email);
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }
        } else {
            // 빈 권한 처리
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    null,
                    null,
                    AuthorityUtils.NO_AUTHORITIES
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }
}