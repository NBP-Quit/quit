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

public interface ReviewService {
	UUID create(UUID reservationId, Long userId, ReviewCreateDto dto, List<MultipartFile> files);

	Slice<ReviewResponse> getAll(UUID storeId, Long userId, Pageable pageable, List<String> tags);

	void update(UUID reviewId, Long userId, ReviewUpdateDto dto, List<MultipartFile> files);

	void delete(UUID reviewId, Long userId);

	void like(UUID reviewId, Long userId);

	void unlike(UUID reviewId, Long userId);

	ReviewSummeryResponse getSummary(UUID storeId, Long userId);
}
