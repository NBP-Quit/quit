package com.quit.review.application.dto;

import java.util.UUID;

import com.quit.review.domain.model.RatingDetails;
import com.quit.review.domain.model.Review;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ReviewUpdateDto {
	private String content;
	private RatingDetailsDto ratingDetails;
}
