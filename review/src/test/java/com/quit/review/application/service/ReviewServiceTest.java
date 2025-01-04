package com.quit.review.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

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

import com.quit.review.application.dto.ReviewCreateDto;
import com.quit.review.application.dto.ScoresDto;
import com.quit.review.common.CustomApiException;
import com.quit.review.domain.model.Review;
import com.quit.review.domain.repository.ReviewRepository;
import com.quit.review.infrastructure.client.ReservationResponse;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

	@InjectMocks
	private ReviewServiceImpl reviewService;

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private ReservationService reservationService;

	@Mock
	private UserService userService;

	@Test
	@DisplayName("리뷰 생성 성공(사진 X)")
	void createReviewSuccessWithoutImages() {
		// given
		UUID storeId = UUID.randomUUID();
		Long userId = 1L;
		UUID reservationId = UUID.randomUUID();
		ReviewCreateDto dto = ReviewCreateDto.builder()
			.reservationId(reservationId)
			.content("정말 너무 너무 맛있었어요! 또 올게요!")
			.scores(new ScoresDto(5, 4, 3, 5))
			.build();

		ReservationResponse mockReservation = ReservationResponse.builder()
			.reservationId(reservationId)
			.storeId(storeId)
			.customerId(userId)
			.guestCount(4)
			.reservationPrice(10000)
			.reservationStatus("COMPLETED")
			.reservationDate(LocalDate.now())
			.reservationTime(LocalTime.of(12, 0))
			.build();

		String mockNickname = "TestUser";

		when(reservationService.getById(reservationId)).thenReturn(mockReservation);
		when(userService.getNicknameById(userId)).thenReturn(mockNickname);

		// when
		reviewService.create(storeId, userId, dto, List.of());

		// then
		verify(reservationService).getById(reservationId);
		verify(userService).getNicknameById(userId);
		verify(reviewRepository).save(any(Review.class));
	}

	@Test
	@DisplayName("예약 상태가 COMPLETED 가 아니라면 예외가 발생한다")
	void createReviewInvalidReservationStatus() {
	    // given
		UUID storeId = UUID.randomUUID();
		Long userId = 1L;
		UUID reservationId = UUID.randomUUID();
		ReviewCreateDto dto = ReviewCreateDto.builder()
			.reservationId(reservationId)
			.content("정말 너무 너무 맛있었어요! 또 올게요!")
			.scores(new ScoresDto(5, 4, 3, 5))
			.build();

		ReservationResponse mockReservation = ReservationResponse.builder()
			.reservationId(reservationId)
			.storeId(storeId)
			.customerId(userId)
			.guestCount(4)
			.reservationPrice(10000)
			.reservationStatus("PENDING")
			.reservationDate(LocalDate.now())
			.reservationTime(LocalTime.of(12, 0))
			.build();

		when(reservationService.getById(reservationId)).thenReturn(mockReservation);

	    // when & then
		assertThatThrownBy(() -> reviewService.create(storeId, userId, dto, List.of()))
			.isInstanceOf(CustomApiException.class)
			.hasMessage("방문을 한 이후에 리뷰를 작성할 수 있습니다.");
	}

	@Test
	@DisplayName("userId와 에약정보의 customerId가 일치하지 않으면 예외가 발생한다.")
	void createReviewInvalidUserPermission() {
	    // given
		UUID storeId = UUID.randomUUID();
		Long userId = 1L;
		UUID reservationId = UUID.randomUUID();
		ReviewCreateDto dto = ReviewCreateDto.builder()
			.reservationId(reservationId)
			.content("정말 너무 너무 맛있었어요! 또 올게요!")
			.scores(new ScoresDto(5, 4, 3, 5))
			.build();

		ReservationResponse mockReservation = ReservationResponse.builder()
			.reservationId(reservationId)
			.storeId(storeId)
			.customerId(2L)
			.guestCount(4)
			.reservationPrice(10000)
			.reservationStatus("COMPLETED")
			.reservationDate(LocalDate.now())
			.reservationTime(LocalTime.of(12, 0))
			.build();

		when(reservationService.getById(reservationId)).thenReturn(mockReservation);

	    // when & then
		assertThatThrownBy(() -> reviewService.create(storeId, userId, dto, List.of()))
			.isInstanceOf(CustomApiException.class)
			.hasMessage("리뷰를 작성할 권한이 없습니다.");
	}
}