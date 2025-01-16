package com.quit.store.infrastructure.redis;

import com.quit.store.application.dto.res.GetReservationSlotResponse;
import com.quit.store.domain.entity.ReservationSlot;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void updateSlotCache(ReservationSlot slot) {
        String cacheKey = generateCacheKey(slot);
        redisTemplate.opsForValue().set(cacheKey, GetReservationSlotResponse.from(slot), Duration.ofHours(1));
    }

    public void deleteSlotCache(ReservationSlot slot) {
        String cacheKey = generateCacheKey(slot);
        redisTemplate.delete(cacheKey);
    }

    private String generateCacheKey(ReservationSlot reservationSlot) {
        return "reservationSlot::" + reservationSlot.getStore().getId() + "_" + reservationSlot.getDate().toString() + "_" + reservationSlot.getTime().toString();
    }

}
