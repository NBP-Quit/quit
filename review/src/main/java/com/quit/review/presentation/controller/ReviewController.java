package com.quit.review.presentation.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.quit.review.application.service.ReviewService;
import com.quit.review.common.ApiResponse;
import com.quit.review.presentation.request.ReviewCreateRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping("/stores/{storeId}/reviews")
	public ResponseEntity<ApiResponse<?>> create(
		@PathVariable UUID storeId,
		@RequestHeader(value = "X-User-ID") String userId,
		@RequestPart("review") @Valid ReviewCreateRequest request,
		@RequestPart(value = "files", required = false) List<MultipartFile> files
	) {
		reviewService.create(storeId, Long.parseLong(userId), request.toDto(), files);
		return null;
	}
}
