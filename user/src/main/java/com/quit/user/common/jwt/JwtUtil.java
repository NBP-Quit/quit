package com.quit.user.common.jwt;

import com.quit.user.application.dto.TokenDto;
import com.quit.user.common.security.UserDetailsServiceImpl;
import com.quit.user.domain.enums.UserRoleEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {


    private final SecretKey secretKey;
    private final UserDetailsServiceImpl userDetailsService;

    private static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Value(("${jwt.access-expiration}"))
    private Long accessExpiration;

    public static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    public JwtUtil(@Value("${jwt.secret.key}")String secretKey, UserDetailsServiceImpl userDetailsService) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
        this.userDetailsService = userDetailsService;
    }

    public TokenDto createToken(Long id, String email, UserRoleEnum role, String nickname) {

        return TokenDto.of(Jwts.builder()
                .subject(String.valueOf(id))
                .claim("email",email)
                .claim("role", role)
                .claim("nickname", nickname)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(secretKey)
                .compact());
    }

    // 인증 처리
    public void setAuthentication(String email) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        // 사용자 정보를 기반으로 인증 토큰 생성
        Authentication authentication = createAuthentication(email, null);
        // SecurityContext에 인증 정보 설정
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    public Authentication createAuthentication(String email, String password) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

    public long getExpiration(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        long iat = claims.get("iat", Long.class); // 발행 시간 (UNIX 타임스탬프)
        long exp = claims.get("exp", Long.class); // 만료 시간 (UNIX 타임스탬프)
        Date issuedAt = new Date(iat * 1000); // 밀리초 단위 변환
        Date expiration = new Date(exp * 1000); // 밀리초 단위 변환

        // 만료 시간 추출
        return expiration.getTime() - System.currentTimeMillis(); // UNIX 타임스탬프 반환
    }
    //토큰 추출
    public String extractToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (token != null && token.startsWith(BEARER_PREFIX)) {
            token = token.substring(BEARER_PREFIX.length());
            return token;
        }else {
            log.error("JWT token이 존재하지 않습니다.");
            throw new JwtException("JWT token이 존재하지 않습니다.");
        }
    }
}
