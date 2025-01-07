package com.quit.review.application.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUploader {
	String upload(MultipartFile file, String filename);

	void delete(String key);
}
