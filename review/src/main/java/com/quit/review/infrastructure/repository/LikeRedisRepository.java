package com.quit.review.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.quit.review.domain.repository.LikeRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LikeRedisRepository implements LikeRepository {

	private final RedisTemplate<String, Object> redisTemplate;

	private static final String REVIEW_LIKES_KEY_PATTERN = "review:%s:likes";
	private static final String STORE_REVIEW_LIKES_KEY_PATTERN = "store:%s:reviewLikes";

	@Override
	public Long add(UUID reviewId, Long userId) {
		String reviewLikeKey = String.format(REVIEW_LIKES_KEY_PATTERN, reviewId);
		return redisTemplate.opsForSet().add(reviewLikeKey, userId.toString());
	}

	@Override
	public Long remove(UUID reviewId, Long userId) {
		String key = String.format(REVIEW_LIKES_KEY_PATTERN, reviewId);
		return redisTemplate.opsForSet().remove(key, userId.toString());
	}

	@Override
	public boolean exist(UUID reviewId, Long userId) {
		String key = String.format(REVIEW_LIKES_KEY_PATTERN, reviewId);
		return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId.toString()));
	}

	@Override
	public int count(UUID storeId, UUID reviewId) {
		String key = String.format(STORE_REVIEW_LIKES_KEY_PATTERN, storeId);
		Double score = redisTemplate.opsForZSet().score(key, reviewId.toString());
		return score != null ? score.intValue() : 0;
	}

	@Override
	public void incrementScore(UUID storeId, UUID reviewId) {
		String key = String.format(STORE_REVIEW_LIKES_KEY_PATTERN, storeId);
		redisTemplate.opsForZSet().incrementScore(key, reviewId.toString(), 1.0);
	}

	@Override
	public void decrementScore(UUID storeId, UUID reviewId) {
		String key = String.format(STORE_REVIEW_LIKES_KEY_PATTERN, storeId);
		redisTemplate.opsForZSet().incrementScore(key, reviewId.toString(), -1);
	}
}
