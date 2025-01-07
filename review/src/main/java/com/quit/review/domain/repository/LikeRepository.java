package com.quit.review.domain.repository;

import java.util.UUID;

public interface LikeRepository {

	Long add(UUID reviewId, Long userId);

	Long remove(UUID reviewId, Long userId);

	boolean exist(UUID reviewId, Long userId);

	int count(UUID storeId, UUID reviewId);

	void incrementScore(UUID storeId, UUID reviewId);

	void decrementScore(UUID storeId, UUID reviewId);
}
