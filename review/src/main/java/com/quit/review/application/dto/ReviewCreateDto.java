package com.quit.review.application.dto;

import java.util.UUID;

import com.quit.review.domain.model.Review;
import com.quit.review.domain.model.RatingDetails;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ReviewCreateDto {
	private String content;
	private RatingDetailsDto ratingDetails;

	public Review toEntity(UUID reservationId, UUID storeId, Long userId, String nickname) {
		return Review.builder()
			.reservationId(reservationId)
			.storeId(storeId)
			.userId(userId)
			.nickname(nickname)
			.content(content)
			.ratingDetails(
				RatingDetails.builder()
					.taste(ratingDetails.getTaste())
					.ambience(ratingDetails.getAmbience())
					.kindness(ratingDetails.getKindness())
					.cleanliness(ratingDetails.getCleanliness())
					.build()
			).build();
	}
}
