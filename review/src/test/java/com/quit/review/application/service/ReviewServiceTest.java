package com.quit.review.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import com.quit.review.application.dto.RatingDetailsDto;
import com.quit.review.application.dto.ReviewCreateDto;
import com.quit.review.common.CustomApiException;
import com.quit.review.domain.model.RatingDetails;
import com.quit.review.domain.model.Review;
import com.quit.review.domain.repository.ReviewRepository;
import com.quit.review.infrastructure.client.ReservationResponse;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
class ReviewServiceTest {

	@InjectMocks
	private ReviewServiceImpl reviewService;

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private ReservationService reservationService;

	@Test
	@DisplayName("리뷰 생성 성공")
	void createReviewSuccessWithoutImages() {
		// given
		UUID storeId = UUID.randomUUID();
		Long userId = 1L;
		String email = "email@email.com";
		UUID reservationId = UUID.randomUUID();
		ReviewCreateDto dto = ReviewCreateDto.builder()
			.content("정말 너무 너무 맛있었어요! 또 올게요!")
			.ratingDetails(new RatingDetailsDto(5, 4, 3, 5))
			.build();

		ReservationResponse mockReservation = ReservationResponse.builder()
			.reservationId(reservationId)
			.storeId(storeId)
			.customerId(email)
			.guestCount(4)
			.reservationPrice(10000)
			.reservationStatus("COMPLETED")
			.reservationDate(LocalDate.now())
			.reservationTime(LocalTime.of(12, 0))
			.build();

		String mockNickname = "TestUser";

		Review mockReview = Review.builder()
			.id(UUID.randomUUID())  // ID 값을 생성
			.reservationId(reservationId)
			.storeId(storeId)
			.userId(userId)
			.content(dto.getContent())
			.ratingDetails(
				RatingDetails.builder()
					.taste(dto.getRatingDetails().getTaste())
					.ambience(dto.getRatingDetails().getAmbience())
					.cleanliness(dto.getRatingDetails().getCleanliness())
					.kindness(dto.getRatingDetails().getKindness())
					.build()
			)
			.nickname(mockNickname)
			.build();

		given(reservationService.getById(reservationId)).willReturn(mockReservation);
		given(reviewRepository.save(any(Review.class))).willReturn(mockReview);

		// when
		UUID reviewId = reviewService.create(storeId, reservationId, userId, email, dto, List.of());

		// then
		then(reservationService).should().getById(reservationId);
		then(reviewRepository).should().save(any(Review.class));
		assertThat(reviewId).isEqualTo(mockReview.getId());
	}

	@Test
	@DisplayName("예약 상태가 COMPLETED 가 아니라면 예외가 발생한다")
	void createReviewInvalidReservationStatus() {
	    // given
		UUID storeId = UUID.randomUUID();
		Long userId = 1L;
		String email = "email@email.com";
		UUID reservationId = UUID.randomUUID();
		ReviewCreateDto dto = ReviewCreateDto.builder()
			.content("정말 너무 너무 맛있었어요! 또 올게요!")
			.ratingDetails(new RatingDetailsDto(5, 4, 3, 5))
			.build();

		ReservationResponse mockReservation = ReservationResponse.builder()
			.reservationId(reservationId)
			.storeId(storeId)
			.customerId(email)
			.guestCount(4)
			.reservationPrice(10000)
			.reservationStatus("PENDING")
			.reservationDate(LocalDate.now())
			.reservationTime(LocalTime.of(12, 0))
			.build();

		given(reservationService.getById(reservationId)).willReturn(mockReservation);

	    // when & then
		assertThatThrownBy(() -> reviewService.create(storeId, reservationId, userId, email, dto, List.of()))
			.isInstanceOf(CustomApiException.class)
			.hasMessage("방문을 한 이후에 리뷰를 작성할 수 있습니다.");
	}
}