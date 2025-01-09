package com.quit.review.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.quit.review.domain.model.Review;
import com.quit.review.infrastructure.repository.ReviewRepositoryCustom;

public interface ReviewRepository extends JpaRepository<Review, UUID>, ReviewRepositoryCustom {

	@Query("SELECT r FROM Review r LEFT JOIN FETCH r.images LEFT JOIN r.likes l " +
		"WHERE r.isDeleted = false " +
		"GROUP BY r.id " +
		"ORDER BY COUNT(l.id) DESC")
	List<Review> findTop5ReviewByLikeCount(Pageable pageable);
}
