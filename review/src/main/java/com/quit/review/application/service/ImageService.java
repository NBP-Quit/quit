package com.quit.review.application.service;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
	void create(MultipartFile file, UUID reviewId);
}
