package com.quit.review.domain.model;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.quit.review.common.CustomApiException;

class ReviewTest {

	@DisplayName("여러 별점들의 평균을 구할 수 있다.")
	@Test
	void calculateAverageScore() {
		// given
		Review review = Review.builder()
			.ratingDetails(RatingDetails.builder()
				.taste(5)
				.ambience(4)
				.cleanliness(3)
				.kindness(2)
				.build()
			)
			.build();
		// when
		review.applyAverageRating();

		// then
		assertThat(review.getRating()).isEqualTo(3.5);
	}

	@ParameterizedTest
	@DisplayName("시간에 따라 MealType 이 할당된다")
	@MethodSource("provideReservationTimesAndExpectedMealTypes")
	void decideMealType(LocalTime reservationTime, MealType expectedMealType) {
		// given
		Review review = Review.builder()
			.content("테스트 리뷰")
			.build();

		// when
		review.decideMealType(reservationTime);

		// then
		assertThat(review.getMealType()).isEqualTo(expectedMealType);
	}

	private static Stream<Arguments> provideReservationTimesAndExpectedMealTypes() {
		return Stream.of(
			Arguments.of(LocalTime.of(7, 0), MealType.BREAKFAST),  // 아침 시간
			Arguments.of(LocalTime.of(12, 0), MealType.LUNCH),      // 점심 시간
			Arguments.of(LocalTime.of(18, 0), MealType.DINNER),     // 저녁 시간
			Arguments.of(LocalTime.of(6, 0), MealType.BREAKFAST),
			Arguments.of(LocalTime.of(10, 59), MealType.BREAKFAST),
			Arguments.of(LocalTime.of(11, 0), MealType.LUNCH),
			Arguments.of(LocalTime.of(16, 59), MealType.LUNCH),
			Arguments.of(LocalTime.of(17, 0), MealType.DINNER),
			Arguments.of(LocalTime.of(23, 59), MealType.DINNER)
		);
	}

	@Test
	@DisplayName("예약 시간이 유효하지 않으면 에외가 발생한다")
	void decideMealType_invalidTime_throwsException() {
		// given
		Review review = Review.builder()
			.content("테스트 리뷰")
			.build();
		LocalTime invalidTime = LocalTime.of(3, 0);  // 유효하지 않은 시간

		// when & then
		assertThatThrownBy(() -> review.decideMealType(invalidTime))
			.isInstanceOf(CustomApiException.class)
			.hasMessage("에약 시간이 지정된 범위에 포함되지 않습니다.");
	}
}