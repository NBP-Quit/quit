package com.quit.review.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.quit.review.application.dto.ReviewCreateDto;
import com.quit.review.application.dto.ReviewResponse;
import com.quit.review.common.CustomApiException;
import com.quit.review.domain.model.Review;
import com.quit.review.domain.repository.ReviewRepository;
import com.quit.review.infrastructure.client.ReservationResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;

	private final ImageService imageService;
	private final ReservationService reservationService;
	private final UserService userService;

	@Override
	@Transactional
	public UUID create(UUID storeId, Long userId, ReviewCreateDto dto, List<MultipartFile> files) {
		// 예약 정보를 조회하여 예약 상태와 권한을 검증
		ReservationResponse reservation = reservationService.getById(dto.getReservationId());
		validate(userId, reservation);

		// 유저 서비스에서 유저 정보 조회 후 nickname 추출
		String nickname = userService.getNicknameById(userId);
		Review review = dto.toEntity(storeId, userId, nickname);

		// 예약 시간을 기준으로 식사 유형(아침, 점심, 저녁)을 지정
		review.decideMealType(reservation.getReservationTime());

		// 평균 별점 계산 후 저장
		review.applyAverageScore();

		UUID reviewId = reviewRepository.save(review).getId();

		if (!CollectionUtils.isEmpty(files)) {
			files.forEach(file -> imageService.create(file, reviewId));
		}

		return reviewId;
	}

	@Override
	public Slice<ReviewResponse> getAll(UUID storeId, Pageable pageable, List<String> tags) {
		Slice<Review> reviewSlice = reviewRepository.getSliceByStoreIdAndTags(storeId, pageable, tags);

		List<ReviewResponse> reviewResponses = reviewSlice.getContent().stream()
			.map(ReviewResponse::from)
			.toList();

		return new SliceImpl<>(reviewResponses, pageable, reviewSlice.hasNext());
	}

	private void validate(Long userId, ReservationResponse reservation) {
		// 조회한 유저 아이디와 예약 정보의 고객 아이디가 일치하는지 확인
		if (!reservation.getCustomerId().equals(userId)) {
			throw new CustomApiException(HttpStatus.FORBIDDEN, "리뷰를 작성할 권한이 없습니다.");
		}

		// 에약 상태가 "방문 완료" 상태일 시에만 리뷰 작성 가능
		if (!reservation.getReservationStatus().equals("COMPLETED")) {
			throw new CustomApiException(HttpStatus.BAD_REQUEST, "방문을 한 이후에 리뷰를 작성할 수 있습니다.");
		}
	}
}
