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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import com.quit.review.presentation.request.ReviewUpdateRequest;

import jakarta.validation.Valid;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping("/reservations/{reservationId}/reviews")
	public ResponseEntity<ApiResponse<Void>> create(
		@PathVariable UUID reservationId,
		@RequestHeader(value = "X-User-ID") String userId,
		@RequestPart("review") @Valid ReviewCreateRequest request,
		@RequestPart(value = "files", required = false) List<MultipartFile> files
	) {
		UUID reviewId = reviewService.create(reservationId, Long.parseLong(userId), request.toDto(), files);
		URI location = URI.create("/api/reviews/" + reviewId);
		return ResponseEntity.created(location).body(ApiResponse.success(HttpStatus.CREATED, "Review Created"));
	}

	@GetMapping("/stores/{storeId}/reviews")
	public ResponseEntity<ApiResponse<Slice<ReviewResponse>>> getAll(
		@PathVariable UUID storeId,
		@RequestHeader(value = "X-User-ID") String userId,
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
		@RequestParam(required = false) List<String> tags
	) {
		return ResponseEntity.ok(ApiResponse.success(reviewService.getAll(storeId, Long.parseLong(userId), pageable, tags)));
	}

	@PutMapping("/reviews/{reviewId}")
	public ResponseEntity<ApiResponse<Void>> update(
		@PathVariable UUID reviewId,
		@RequestHeader(value = "X-User-ID") String userId,
		@RequestPart("review") @Valid ReviewUpdateRequest request,
		@RequestPart(value = "files", required = false) List<MultipartFile> files
	) {
		reviewService.update(reviewId, Long.parseLong(userId), request.toDto(), files);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Review Updated"));
	}

	@DeleteMapping("/reviews/{reviewId}")
	public ResponseEntity<ApiResponse<Void>> delete(
		@PathVariable UUID reviewId,
		@RequestHeader(value = "X-User-ID") String userId
	) {
		reviewService.delete(reviewId, Long.parseLong(userId));
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Review Deleted"));
	}

	@PostMapping("/stores/{storeId}/reviews/{reviewId}/likes")
	public ResponseEntity<ApiResponse<Void>> like(
		@PathVariable UUID storeId,
		@PathVariable UUID reviewId,
		@RequestHeader(value = "X-User-ID") String userId
	) {
		reviewService.like(storeId, reviewId, Long.parseLong(userId));
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Like Completed"));
	}

	@DeleteMapping("/stores/{storeId}/reviews/{reviewId}/likes")
	public ResponseEntity<ApiResponse<Void>> unlike(
		@PathVariable UUID storeId,
		@PathVariable UUID reviewId,
		@RequestHeader(value = "X-User-ID") String userId
	) {
		reviewService.unlike(storeId, reviewId, Long.parseLong(userId));
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Unlike Completed"));
	}

	// @GetMapping("/stores/{storeId}/reviews/summary")
	// public ResponseEntity<ApiResponse<?>> getSummary(
	// 	@PathVariable UUID storeId
	// ) {
	// 	reviewService.getSummary()
	// }
}
