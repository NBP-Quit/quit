package com.quit.review.application.service;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeEvent {
	private UUID reviewId;
	private Long userId;
	private LikeAction action;

	public static LikeEvent create(UUID reviewId, Long userId, LikeAction action) {
		return LikeEvent.builder()
			.reviewId(reviewId)
			.userId(userId)
			.action(action)
			.build();
	}
}
