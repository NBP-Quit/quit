package com.quit.user.common.jwt;

import com.quit.user.application.dto.TokenDto;
import com.quit.user.domain.enums.UserRoleEnum;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {


    private final SecretKey secretKey;

    @Value(("${jwt.access-expiration}"))
    private Long accessExpiration;

    public static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    public JwtUtil(@Value("${jwt.secret.key}")String secretKey) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
    }

    public TokenDto createToken(Long id, UserRoleEnum role) {

        log.info("=========== 토큰 생성 시작 +++++++++++++++");
        return TokenDto.of(Jwts.builder()
                .subject(String.valueOf(id))
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(secretKey)
                .compact());
    }

}
