package com.quit.gateway.common.jwt;

import com.quit.gateway.dto.CustomHeader;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import javax.crypto.SecretKey;


@Component
public class JwtUtil {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    public final SecretKey secretKey;
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    public JwtUtil(@Value("${jwt.secret.key}")String secretKey) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
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

    //토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseClaimsJws(token);
            log.info("토큰 유효성 검사 통과");
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // 사용자 정보 담기
    public ServerWebExchange setCustomHeader(ServerWebExchange exchange, CustomHeader response) {

        return exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + response.getToken())
                        .header("X-User-Id", response.getId())
                        .header("X-User-Email", response.getEmail())
                        .header("X-User-Nickname", response.getNickname())
                        .header("X-User-Role", "ROLE_"+response.getRole())
                        .build())
                .build();
    }

    public CustomHeader extractUserDetails(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        log.info(claims.toString());
        String id = claims.getSubject();
        String email = claims.get("email", String.class);
        String nickname = claims.get("nickname", String.class);
        String role = claims.get("role", String.class);

        return CustomHeader.builder()
                .token(token)
                .id(id)
                .email(email)
                .nickname(nickname)
                .role(role)
                .build();
    }
}
