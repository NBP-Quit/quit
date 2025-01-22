package com.quit.payment.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotencyService {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveIdempotencyKey(String idempotencyKey, String paymentKey) {
        log.info(">>>>> redis 저장 <<<<<");
        redisTemplate.opsForValue().set(
                "idempotencyKey::" + idempotencyKey,
                paymentKey,
                Duration.ofDays(15)
        );
    }

    public String getPaymentKeyByIdempotencyKey(String idempotencyKey) {
        log.info(">>>>> redis 조회 <<<<<");
        return redisTemplate.opsForValue().get("idempotencyKey::" + idempotencyKey);
    }

}
