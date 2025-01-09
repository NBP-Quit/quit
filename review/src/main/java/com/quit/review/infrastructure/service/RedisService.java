package com.quit.review.infrastructure.service;

import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.quit.review.application.service.CacheService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService implements CacheService {

	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	public Long addToSet(String key, Object value) {
		return redisTemplate.opsForSet().add(key, value.toString());
	}

	@Override
	public Set<Object> getSetMembers(String key) {
		return redisTemplate.opsForSet().members(key);
	}

	@Override
	public Boolean isMemberOfSet(String key, Object value) {
		return redisTemplate.opsForSet().isMember(key, value);
	}

	@Override
	public Long removeFromSet(String key, Object value) {
		return redisTemplate.opsForSet().remove(key, value);
	}

	@Override
	public Long getSetSize(String key) {
		return redisTemplate.opsForSet().size(key);
	}
}
