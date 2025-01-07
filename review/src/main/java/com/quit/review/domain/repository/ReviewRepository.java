package com.quit.review.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quit.review.domain.model.Review;
import com.quit.review.infrastructure.repository.ReviewRepositoryCustom;

public interface ReviewRepository extends JpaRepository<Review, UUID>, ReviewRepositoryCustom {
}
