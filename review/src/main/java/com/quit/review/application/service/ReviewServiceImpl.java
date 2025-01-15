package com.quit.review.application.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.quit.review.application.dto.RatingDetailsDto;
import com.quit.review.application.dto.ReviewCreateDto;
import com.quit.review.application.dto.ReviewResponse;
import com.quit.review.application.dto.ReviewSummeryResponse;
import com.quit.review.application.dto.ReviewUpdateDto;
import com.quit.review.common.CustomApiException;
import com.quit.review.domain.model.MealType;
import com.quit.review.domain.model.RatingDetails;
import com.quit.review.domain.model.Review;
import com.quit.review.domain.model.Tag;
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

	private final CacheService cacheService;
	private final MessagePublisher messagePublisher;

	private final ImageService imageService;
	private final ReservationService reservationService;

	@Override
	@Transactional
	@CacheEvict(cacheNames = "reviewSummaryCache", key = "args[0]")
	public UUID create(UUID storeId, UUID reservationId, Long userId, String nickname, ReviewCreateDto dto, List<MultipartFile> files) {
		// 예약 정보를 조회하여 예약 상태와 권한을 검증
		ReservationResponse reservation = reservationService.getById(reservationId);
		validateReservationStatus(reservation.getReservationStatus());

		Review review = dto.toEntity(reservationId, storeId, userId, nickname);

		// 예약 시간을 기준으로 식사 유형(아침, 점심, 저녁)을 지정
		review.decideMealType(reservation.getReservationTime());

		// 평균 별점 계산 후 저장
		review.applyAverageRating();

		Review savedReview = reviewRepository.save(review);

		if (!CollectionUtils.isEmpty(files)) {
			files.forEach(file -> imageService.create(file, savedReview));
		}

		// 가게의 총 평균 별점 및 항목 별 평균 별점 업데이트
		updateStoreRating(storeId, review.getRatingDetails(), 1);

		return savedReview.getId();
	}

	@Override
	public Slice<ReviewResponse> getAll(UUID storeId, Long userId, Pageable pageable, Tag tag, MealType mealType) {
		Slice<Review> reviewSlice = reviewRepository.getSliceByStoreIdAndTags(storeId, pageable, tag, mealType);

		List<ReviewResponse> reviewResponses = reviewSlice.getContent().stream()
			.map(review -> {
				// TODO: 추후에 요청을 하나로 묶는 Redis Pipeline 으로 최적화 고려해볼 것
				String key = "review:" + review.getId() + ":likes";
				int likeCount = cacheService.getSetSize(key).intValue();
				boolean isLiked = cacheService.isMemberOfSet(key, userId);
				return ReviewResponse.from(review, likeCount, isLiked);
			})
			.toList();

		return new SliceImpl<>(reviewResponses, pageable, reviewSlice.hasNext());
	}

	@Override
	@Transactional
	@CacheEvict(cacheNames = "reviewSummaryCache", key = "args[0]")
	public void update(UUID storeId, UUID reviewId, Long userId, String role, ReviewUpdateDto dto, List<MultipartFile> files) {
		Review review = reviewRepository.findByIdWithImages(reviewId)
			.orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "Review not found"));

		validateRole(review, userId, role);

		updateStoreRating(storeId, review.getRatingDetails(), -1);

		RatingDetailsDto ratingDetails = dto.getRatingDetails();

		review.updateRatingDetails(ratingDetails.getTaste(), ratingDetails.getAmbience(), ratingDetails.getKindness(), ratingDetails.getCleanliness());
		review.updateContent(dto.getContent());

		if (review.getImages().isEmpty()) {
			// 이미지 파일이 있다면 해당 리뷰로 저장된 모든 이미지 삭제 후 재업로드
			imageService.deleteAll(review);
			if (!CollectionUtils.isEmpty(files)) {
				files.forEach(file -> {
					imageService.create(file, review);
				});
			}
		}

		updateStoreRating(storeId, review.getRatingDetails(), 1);
	}

	@Override
	@Transactional
	@CacheEvict(cacheNames = "reviewSummaryCache", key = "args[0]")
	public void delete(UUID storeId, UUID reviewId, Long userId, String role) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "Review not found"));
		review.delete();

		imageService.deleteAll(review);

		unlike(storeId, reviewId, userId);
		updateStoreRating(review.getStoreId(), review.getRatingDetails(), -1);
	}

	@Override
	@Transactional
	@CacheEvict(cacheNames = "reviewSummaryCache", key = "args[0]")
	public void like(UUID storeId, UUID reviewId, Long userId) {
		String key = "review:" + reviewId + ":likes";
		Long added = cacheService.addToSet(key, userId);

		if (added > 0) {
			LikeEvent likeEvent = LikeEvent.create(reviewId, userId, LikeAction.LIKE);
			messagePublisher.publishLikeEvent(likeEvent);
		}
	}

	@Override
	@Transactional
	@CacheEvict(cacheNames = "reviewSummaryCache", key = "args[0]")
	public void unlike(UUID storeId, UUID reviewId, Long userId) {
		String key = "review:" + reviewId + ":likes";
		Long removed = cacheService.removeFromSet(key, userId);

		if (removed > 0) {
			LikeEvent likeEvent = LikeEvent.create(reviewId, userId, LikeAction.UNLIKE);
			messagePublisher.publishLikeEvent(likeEvent);
		}
	}

	@Override
	@Cacheable(cacheNames = "reviewSummaryCache", key = "args[0]")
	public ReviewSummeryResponse getSummary(UUID storeId, Long userId) {
		String avgKey = "store:" + storeId + ":averages";
		String countKey = "store:" + storeId + ":count";

		Map<Object, Object> averages = cacheService.getAll(avgKey);

		int count = (int) cacheService.getValue(countKey);

		List<Review> reviews = reviewRepository.findTop5ReviewByLikeCount();
		List<ReviewResponse> reviewResponses = reviews.stream()
			.map(review -> {
				String key = "review:" + review.getId() + ":likes";
				int likeCount = cacheService.getSetSize(key).intValue();
				boolean isLiked = cacheService.isMemberOfSet(key, userId);
				return ReviewResponse.from(review, likeCount, isLiked);
			})
			.toList();

		return ReviewSummeryResponse.from(averages, count, reviewResponses);
	}

	private void validateRole(Review review, Long userId, String role) {
		if (!role.equals("ROLE_MASTER") && !review.getUserId().equals(userId)) {
			throw new CustomApiException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
		}
	}

	private void updateStoreRating(UUID storeId, RatingDetails ratingDetails, long delta) {
		String key = "store:" + storeId + ":rating";
		String countKey = "store:" + storeId + ":count";
		String avgKey = "store:" + storeId + ":averages";

		cacheService.increaseForHash(key, "sum_taste", ratingDetails.getTaste() * delta);
		cacheService.increaseForHash(key, "sum_ambience", ratingDetails.getAmbience() * delta);
		cacheService.increaseForHash(key, "sum_kindness", ratingDetails.getKindness() * delta);
		cacheService.increaseForHash(key, "sum_cleanliness", ratingDetails.getCleanliness() * delta);
		cacheService.increaseForValue(countKey, delta);

		int sumTaste = (int) cacheService.getHashValue(key, "sum_taste");
		int sumAmbience = (int) cacheService.getHashValue(key, "sum_ambience");
		int sumKindness = (int) cacheService.getHashValue(key, "sum_kindness");
		int sumCleanliness = (int) cacheService.getHashValue(key, "sum_cleanliness");
		int count = (int) cacheService.getValue(countKey);

		double avgTaste = sumTaste / (double) count;
		double avgAmbience = sumAmbience / (double) count;
		double avgKindness = sumKindness / (double) count;
		double avgCleanliness = sumCleanliness / (double) count;
		double avgTotal = (avgTaste + avgAmbience + avgKindness + avgCleanliness) / 4.0;

		Map<String, Object> averages = new HashMap<>();
		averages.put("avg_taste", avgTaste);
		averages.put("avg_ambience", avgAmbience);
		averages.put("avg_kindness", avgKindness);
		averages.put("avg_cleanliness", avgCleanliness);
		averages.put("avg_total", avgTotal);

		cacheService.putAll(avgKey, averages);
	}

	private void validateReservationStatus(String reservationStatus) {
		// 에약 상태가 "방문 완료" 상태일 시에만 리뷰 작성 가능
		if (!reservationStatus.equals("COMPLETED")) {
			throw new CustomApiException(HttpStatus.BAD_REQUEST, "방문을 한 이후에 리뷰를 작성할 수 있습니다.");
		}
	}
}
