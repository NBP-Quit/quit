package com.quit.review.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import com.quit.review.application.dto.ReviewCreateDto;
import com.quit.review.application.dto.ReviewResponse;

public interface ReviewService {
	UUID create(UUID storeId, Long userId, ReviewCreateDto dto, List<MultipartFile> files);

	Slice<ReviewResponse> getAll(UUID storeId, Pageable pageable, List<String> tags);
}
