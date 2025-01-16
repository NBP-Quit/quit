package com.quit.review.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import com.quit.review.application.dto.ReviewCreateDto;
import com.quit.review.application.dto.ReviewResponse;
import com.quit.review.application.dto.ReviewSummeryResponse;
import com.quit.review.application.dto.ReviewUpdateDto;
import com.quit.review.domain.model.MealType;
import com.quit.review.domain.model.Tag;

public interface ReviewService {
	UUID create(UUID storeId, UUID reservationId, Long userId, String nickname, ReviewCreateDto dto, List<MultipartFile> files);

	Slice<ReviewResponse> getAll(UUID storeId, Long userId, Pageable pageable, Tag tag, MealType mealType);

	void update(UUID storeId, UUID reviewId, Long userId, String role, ReviewUpdateDto dto, List<MultipartFile> files);

	void delete(UUID storeId, UUID reviewId, Long userId, String role);

	void like(UUID storeId, UUID reviewId, Long userId);

	void unlike(UUID storeId, UUID reviewId, Long userId);

	ReviewSummeryResponse getSummary(UUID storeId, Long userId);
}
