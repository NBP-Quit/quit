package com.quit.review.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.quit.review.domain.model.Review;
import com.quit.review.infrastructure.repository.ReviewRepositoryCustom;

public interface ReviewRepository extends JpaRepository<Review, UUID>, ReviewRepositoryCustom {

	@Query("SELECT r FROM Review r LEFT JOIN FETCH r.images WHERE r.id = :reviewId")
	Optional<Review> findByIdWithImages(@Param("reviewId") UUID reviewId);

	@Query(value = "SELECT r.* FROM p_review r " +
		"LEFT JOIN p_like l ON r.id = l.review_id " +
		"WHERE r.is_deleted = false " +
		"GROUP BY r.id " +
		"ORDER BY COUNT(l.id) DESC " +
		"LIMIT 5",
		nativeQuery = true)
	List<Review> findTop5ReviewByLikeCount();
}
