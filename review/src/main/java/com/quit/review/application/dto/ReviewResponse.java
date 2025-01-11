package com.quit.review.application.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.quit.review.domain.model.Image;
import com.quit.review.domain.model.MealType;
import com.quit.review.domain.model.Review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewResponse {
	private UUID id;
	private List<String> images;
	private double rating;
	private RatingDetailsDto ratingDetails;
	private String nickname;
	private String content;
	private MealType mealType;
	private int likeCount;
	private boolean isLiked;
	private int replyCount;
	private LocalDate createdAt;

	public static ReviewResponse from(Review review, int likeCount, boolean isLiked) {
		List<String> images = review.getImages().stream()
			.map(Image::getUrl)
			.toList();

		return ReviewResponse.builder()
			.id(review.getId())
			.images(images)
			.rating(review.getRating())
			.ratingDetails(RatingDetailsDto.from(review.getRatingDetails()))
			.nickname(review.getNickname())
			.content(review.getContent())
			.mealType(review.getMealType())
			.likeCount(likeCount)
			.isLiked(isLiked)
			.replyCount(review.getReplyCount())
			.createdAt(review.getCreatedAt().toLocalDate())
			.build();
	}
}
