package com.quit.review.application.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewSummeryResponse {
	private int reviewCount;
	private AvgScoresDto avgScores;
	private List<ReviewResponse> reviews;

	public static ReviewSummeryResponse from(AvgScoresDto avgScores, int count, List<ReviewResponse> reviews) {
		return ReviewSummeryResponse.builder()
			.reviewCount(count)
			.avgScores(avgScores)
			.reviews(reviews)
			.build();
	}
}
