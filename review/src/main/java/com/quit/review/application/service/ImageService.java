package com.quit.review.application.service;

import org.springframework.web.multipart.MultipartFile;

import com.quit.review.domain.model.Review;

public interface ImageService {
	void create(MultipartFile file, Review review);

	void deleteAll(Review review);
}
