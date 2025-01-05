package com.quit.review.infrastructure.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUploader {
	String upload(MultipartFile file, String filename);
}
