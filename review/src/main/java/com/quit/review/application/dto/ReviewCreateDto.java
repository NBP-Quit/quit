package com.quit.review.application.dto;

import java.util.UUID;

import com.quit.review.domain.model.Review;
import com.quit.review.domain.model.Scores;

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
	private UUID reservationId;
	private String content;
	private ScoresDto scores;

	public Review toEntity(UUID storeId, Long userId, String nickname) {
		return Review.builder()
			.storeId(storeId)
			.userId(userId)
			.nickname(nickname)
			.reservationId(reservationId)
			.content(content)
			.scores(
				Scores.builder()
					.taste(scores.getTaste())
					.ambience(scores.getAmbience())
					.kindness(scores.getKindness())
					.cleanliness(scores.getCleanliness())
					.build()
			).build();
	}
}
