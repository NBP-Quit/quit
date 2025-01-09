package com.quit.review.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quit.review.domain.model.Like;
import com.quit.review.domain.model.Review;

public interface LikeRepository extends JpaRepository<Like, UUID> {
	Optional<Like> findByReviewAndUserId(Review review, Long userId);
}
