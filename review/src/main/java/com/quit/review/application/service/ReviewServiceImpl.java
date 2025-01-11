package com.quit.review.application.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.quit.review.application.dto.AvgScoresDto;
import com.quit.review.application.dto.ReviewCreateDto;
import com.quit.review.application.dto.ReviewResponse;
import com.quit.review.application.dto.ReviewSummeryResponse;
import com.quit.review.application.dto.ReviewUpdateDto;
import com.quit.review.application.dto.ScoresDto;
import com.quit.review.common.CustomApiException;
import com.quit.review.domain.model.MealType;
import com.quit.review.domain.model.Review;
import com.quit.review.domain.model.Scores;
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
	private final UserService userService;

	@Override
	@Transactional
	public UUID create(UUID reservationId, Long userId, ReviewCreateDto dto, List<MultipartFile> files) {
		// 예약 정보를 조회하여 예약 상태와 권한을 검증
		ReservationResponse reservation = reservationService.getById(reservationId);
		validate(userId, reservation);

		UUID storeId = reservation.getStoreId();

		// 유저 서비스에서 유저 정보 조회 후 nickname 추출
		String nickname = userService.getNicknameById(userId);
		Review review = dto.toEntity(reservationId, storeId, userId, nickname);

		// 예약 시간을 기준으로 식사 유형(아침, 점심, 저녁)을 지정
		review.decideMealType(reservation.getReservationTime());

		// 평균 별점 계산 후 저장
		review.applyAverageScore();

		Review savedReview = reviewRepository.save(review);

		if (!CollectionUtils.isEmpty(files)) {
			files.forEach(file -> imageService.create(file, savedReview));
		}

		// 가게의 총 평균 별점 및 항목 별 평균 별점 업데이트
		updateStoreScores(storeId, review.getScores(), 1);

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
	public void update(UUID reviewId, Long userId, ReviewUpdateDto dto, List<MultipartFile> files) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "Review not found"));

		updateStoreScores(review.getStoreId(), review.getScores(), -1);

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

		updateStoreScores(review.getStoreId(), review.getScores(), 1);
	}

	@Override
	@Transactional
	public void delete(UUID reviewId, Long userId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "Review not found"));
		review.delete();

		imageService.deleteAll(review);

		unlike(reviewId, userId);
		updateStoreScores(review.getStoreId(), review.getScores(), -1);
	}

	@Override
	@Transactional
	public void like(UUID reviewId, Long userId) {
		String key = "review:" + reviewId + ":likes";
		Long added = cacheService.addToSet(key, userId);

		if (added > 0) {
			LikeEvent likeEvent = LikeEvent.create(reviewId, userId, LikeAction.LIKE);
			messagePublisher.publishLikeEvent(likeEvent);
		}
	}

	@Override
	@Transactional
	public void unlike(UUID reviewId, Long userId) {
		String key = "review:" + reviewId + ":likes";
		Long removed = cacheService.removeFromSet(key, userId);

		if (removed > 0) {
			LikeEvent likeEvent = LikeEvent.create(reviewId, userId, LikeAction.UNLIKE);
			messagePublisher.publishLikeEvent(likeEvent);
		}
	}

	@Override
	public ReviewSummeryResponse getSummary(UUID storeId, Long userId) {
		String avgKey = "store:" + storeId + ":averages";
		String countKey = "store:" + storeId + ":scores";

		Map<Object, Object> averages = cacheService.getAll(avgKey);

		AvgScoresDto avgScoresDto = AvgScoresDto.from(averages);

		Long count = (Long) cacheService.getValue(countKey);

		Pageable pageable = PageRequest.of(0, 5);
		List<Review> reviews = reviewRepository.findTop5ReviewByLikeCount(pageable);
		List<ReviewResponse> reviewResponses = reviews.stream()
			.map(review -> {
				String key = "review:" + review.getId() + ":likes";
				int likeCount = cacheService.getSetSize(key).intValue();
				boolean isLiked = cacheService.isMemberOfSet(key, userId);
				return ReviewResponse.from(review, likeCount, isLiked);
			})
			.toList();

		return ReviewSummeryResponse.from(avgScoresDto, count.intValue(), reviewResponses);
	}

	private void updateStoreScores(UUID storeId, Scores scores, long delta) {
		String key = "store:" + storeId + ":scores";
		String countKey = "store:" + storeId + ":count";
		String avgKey = "store:" + storeId + ":averages";

		cacheService.increaseForHash(key, "sum_taste", scores.getTaste() * delta);
		cacheService.increaseForHash(key, "sum_ambience", scores.getAmbience() * delta);
		cacheService.increaseForHash(key, "sum_kindness", scores.getKindness() * delta);
		cacheService.increaseForHash(key, "sum_cleanliness", scores.getCleanliness() * delta);
		cacheService.increaseForValue(countKey, delta);

		Long sumTaste = (Long) cacheService.getHashValue(key, "sum_taste");
		Long sumAmbience = (Long) cacheService.getHashValue(key, "sum_ambience");
		Long sumKindness = (Long) cacheService.getHashValue(key, "sum_kindness");
		Long sumCleanliness = (Long) cacheService.getHashValue(key, "sum_cleanliness");
		Long count = (Long) cacheService.getValue(countKey);

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
