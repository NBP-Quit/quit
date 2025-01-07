package com.quit.review.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.quit.review.common.CustomApiException;
import com.quit.review.domain.model.Image;
import com.quit.review.domain.model.Review;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageServiceImpl implements ImageService {

	private final ImageUploader imageUploader;

	@Override
	@Transactional
	public void create(MultipartFile file, Review review) {
		String originalFilename = file.getOriginalFilename();
		String extension = extractExtension(originalFilename);
		validateExtension(extension);

		String filename = UUID.randomUUID() + "." + extension;

		String url = imageUploader.upload(file, filename);

		Image image = Image.create(url, filename, originalFilename, file.getContentType(), file.getSize());
		review.addImage(image);
	}

	@Override
	@Transactional
	public void deleteAll(Review review) {
		review.getImages().forEach(image -> {
			image.delete();
			imageUploader.delete(image.getUrl());
		});

	}

	private String extractExtension(String filename) {
		int lastDotIndex = filename.lastIndexOf(".");
		if (lastDotIndex == -1) {
			throw new CustomApiException(HttpStatus.BAD_REQUEST, "이미지 파일의 확장자를 찾을 수 없습니다.");
		}

		return filename.substring(lastDotIndex + 1).toLowerCase();
	}

	private void validateExtension(String extension) {
		List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "gif", "webp");
		if (!allowedExtensions.contains(extension)) {
			throw new CustomApiException(HttpStatus.BAD_REQUEST, "이미지 파일만 업로드할 수 있습니다.");
		}
	}
}
