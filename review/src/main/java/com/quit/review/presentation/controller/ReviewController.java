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
import com.quit.review.application.dto.ReviewSummeryResponse;
import com.quit.review.application.service.ReviewService;
import com.quit.review.common.ApiResponse;
import com.quit.review.domain.model.MealType;
import com.quit.review.domain.model.Tag;
import com.quit.review.presentation.request.ReviewCreateRequest;
import com.quit.review.presentation.request.ReviewUpdateRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping("/stores/{storeId}/reservations/{reservationId}/reviews")
	public ResponseEntity<ApiResponse<Void>> create(
		@PathVariable UUID storeId,
		@PathVariable UUID reservationId,
		@RequestHeader(value = "X-User-ID") String userId,
		@RequestHeader(value = "X-User-Nickname") String nickname,
		@RequestPart("review") @Valid ReviewCreateRequest request,
		@RequestPart(value = "files", required = false) List<MultipartFile> files
	) {
		UUID reviewId = reviewService.create(storeId, reservationId, Long.parseLong(userId), nickname, request.toDto(), files);
		URI location = URI.create("/api/reviews/" + reviewId);
		return ResponseEntity.created(location).body(ApiResponse.success(HttpStatus.CREATED, "Review Created"));
	}

	@GetMapping("/stores/{storeId}/reviews")
	public ResponseEntity<ApiResponse<Slice<ReviewResponse>>> getAll(
		@PathVariable UUID storeId,
		@RequestHeader(value = "X-User-ID") String userId,
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
		@RequestParam(required = false) Tag tag,
		@RequestParam(required = false) MealType mealType
	) {
		return ResponseEntity.ok(ApiResponse.success(reviewService.getAll(storeId, Long.parseLong(userId), pageable, tag, mealType)));
	}

	@PutMapping("/stores/{storeId}/reviews/{reviewId}")
	public ResponseEntity<ApiResponse<Void>> update(
		@PathVariable UUID storeId,
		@PathVariable UUID reviewId,
		@RequestHeader(value = "X-User-ID") String userId,
		@RequestHeader(value = "X-User-Role") String role,
		@RequestPart("review") @Valid ReviewUpdateRequest request,
		@RequestPart(value = "files", required = false) List<MultipartFile> files
	) {
		reviewService.update(storeId, reviewId, Long.parseLong(userId), role, request.toDto(), files);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Review Updated"));
	}

	@DeleteMapping("/stores/{storeId}/reviews/{reviewId}")
	public ResponseEntity<ApiResponse<Void>> delete(
		@PathVariable UUID storeId,
		@PathVariable UUID reviewId,
		@RequestHeader(value = "X-User-ID") String userId,
		@RequestHeader(value = "X-User-Role") String role
		) {
		reviewService.delete(storeId, reviewId, Long.parseLong(userId), role);
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

	@GetMapping("/stores/{storeId}/reviews/summary")
	public ResponseEntity<ApiResponse<ReviewSummeryResponse>> getSummary(
		@PathVariable UUID storeId,
		@RequestHeader(value = "X-User-ID") String userId
	) {
		return ResponseEntity.ok(ApiResponse.success(reviewService.getSummary(storeId, Long.parseLong(userId))));

	}
}
