package com.quit.review.application.dto;

import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
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
	private double rating;
	private RatingDetails ratingDetails;
	private List<ReviewResponse> reviews;

	public static ReviewSummeryResponse from(Map<Object, Object> averages, int count, List<ReviewResponse> reviews) {
		return ReviewSummeryResponse.builder()
			.reviewCount(count)
			.rating(averages != null ? (double) averages.get("avg_total") : 0.0)
			.ratingDetails(RatingDetails.from(averages))
			.reviews(reviews)
			.build();
	}

	@Builder
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	private static class RatingDetails {
		private double taste;
		private double ambience;
		private double kindness;
		private double cleanliness;

		public static RatingDetails from(Map<Object, Object> averages) {
			return RatingDetails.builder()
				.taste(averages != null ? (double) averages.get("avg_taste") : 0.0)
				.ambience(averages != null ? (double) averages.get("avg_ambience") : 0.0)
				.kindness(averages != null ? (double) averages.get("avg_kindness") : 0.0)
				.cleanliness(averages != null ? (double) averages.get("avg_cleanliness") : 0.0)
				.build();
		}
	}
}
