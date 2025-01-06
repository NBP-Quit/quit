package com.quit.review.presentation.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.quit.review.application.dto.ReviewResponse;
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
	public ResponseEntity<ApiResponse<Void>> create(
		@PathVariable UUID storeId,
		@RequestHeader(value = "X-User-ID") String userId,
		@RequestPart("review") @Valid ReviewCreateRequest request,
		@RequestPart(value = "files", required = false) List<MultipartFile> files
	) {
		UUID reviewId = reviewService.create(storeId, Long.parseLong(userId), request.toDto(), files);
		URI location = URI.create("/api/stores/" + storeId + "/reviews/" + reviewId);
		return ResponseEntity.created(location).body(ApiResponse.success(HttpStatus.CREATED, "Review Created"));
	}

	@GetMapping("/stores/{storeId}/reviews")
	public ResponseEntity<ApiResponse<Slice<ReviewResponse>>> getAll(
		@PathVariable UUID storeId,
		@PageableDefault(sort = "likeCount", direction = Sort.Direction.DESC) Pageable pageable,
		@RequestParam(required = false) List<String> tags
	) {
		return ResponseEntity.ok(ApiResponse.success(reviewService.getAll(storeId, pageable, tags)));
	}
}
