package com.quit.review.infrastructure.service;

import java.util.Map;
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

	@Override
	public void increaseForHash(String key, String hashKey, long delta) {
		redisTemplate.opsForHash().increment(key, hashKey, delta);
	}


	@Override
	public void increaseForValue(String key, long delta) {
		redisTemplate.opsForValue().increment(key, delta);
	}

	@Override
	public Object getHashValue(String key, String hashKey) {
		return redisTemplate.opsForHash().get(key, hashKey);
	}

	@Override
	public Object getValue(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	@Override
	public void putAll(String key, Map<String, Object> map) {
		redisTemplate.opsForHash().putAll(key, map);
	}

	@Override
	public Map<Object, Object> getAll(String key) {
		return redisTemplate.opsForHash().entries(key);
	}
}
