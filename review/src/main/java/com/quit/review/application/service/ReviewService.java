package com.quit.review.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.quit.review.application.dto.ReviewCreateDto;

public interface ReviewService {
	void create(UUID storeId, Long userId, ReviewCreateDto dto, List<MultipartFile> files);
}
