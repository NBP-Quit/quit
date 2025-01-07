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
import com.quit.review.application.dto.ReviewUpdateDto;
import com.quit.review.application.dto.ScoresDto;
import com.quit.review.common.CustomApiException;
import com.quit.review.domain.model.Review;
import com.quit.review.domain.repository.LikeRepository;
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

	private final LikeRepository likeRepository;

	private final ImageService imageService;
	private final ReservationService reservationService;
	private final UserService userService;

	@Override
	@Transactional
	public UUID create(UUID reservationId, Long userId, ReviewCreateDto dto, List<MultipartFile> files) {
		// 예약 정보를 조회하여 예약 상태와 권한을 검증
		ReservationResponse reservation = reservationService.getById(reservationId);
		validate(userId, reservation);

		// 유저 서비스에서 유저 정보 조회 후 nickname 추출
		String nickname = userService.getNicknameById(userId);
		Review review = dto.toEntity(reservationId, reservation.getStoreId(), userId, nickname);

		// 예약 시간을 기준으로 식사 유형(아침, 점심, 저녁)을 지정
		review.decideMealType(reservation.getReservationTime());

		// 평균 별점 계산 후 저장
		review.applyAverageScore();

		Review savedReview = reviewRepository.save(review);

		if (!CollectionUtils.isEmpty(files)) {
			files.forEach(file -> imageService.create(file, savedReview));
		}

		return savedReview.getId();
	}

	@Override
	public Slice<ReviewResponse> getAll(UUID storeId, Long userId, Pageable pageable, List<String> tags) {
		Slice<Review> reviewSlice = reviewRepository.getSliceByStoreIdAndTags(storeId, pageable, tags);

		List<ReviewResponse> reviewResponses = reviewSlice.getContent().stream()
			.map(review -> {
				int likeCount = likeRepository.count(storeId, review.getId());
				boolean isLiked = likeRepository.exist(review.getId(), userId);
				return ReviewResponse.from(review, likeCount, isLiked);
			})
			.toList();

		return new SliceImpl<>(reviewResponses, pageable, reviewSlice.hasNext());
	}

	@Override
	@Transactional
	public void update(UUID reviewId, Long userId, ReviewUpdateDto dto, List<MultipartFile> files) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "Review not found"));

		ScoresDto scores = dto.getScores();
		review.updateScores(scores.getTaste(), scores.getAmbience(), scores.getKindness(), scores.getCleanliness());
		review.updateContent(dto.getContent());

		// 이미지 파일이 있다면 해당 리뷰로 저장된 모든 이미지 삭제 후 재업로드
		if (!CollectionUtils.isEmpty(files)) {
			imageService.deleteAll(review);
			files.forEach(file -> {
				imageService.create(file, review);
			});
		}
	}

	@Override
	@Transactional
	public void delete(UUID reviewId, Long userId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "Review not found"));
		review.delete();
		imageService.deleteAll(review);
	}

	@Override
	@Transactional
	public void like(UUID storeId, UUID reviewId, Long userId) {
		Long added = likeRepository.add(reviewId, userId);
		if (added != null && added > 0) {
			likeRepository.incrementScore(storeId, reviewId);
		}
	}

	@Override
	@Transactional
	public void unlike(UUID storeId, UUID reviewId, Long userId) {
		Long removed = likeRepository.remove(reviewId, userId);
		if (removed != null && removed > 0) {
			likeRepository.decrementScore(storeId, reviewId);
		}
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
