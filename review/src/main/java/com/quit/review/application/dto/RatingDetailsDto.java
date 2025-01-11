package com.quit.review.application.dto;

import com.quit.review.domain.model.RatingDetails;
import com.quit.review.presentation.request.RatingDetailsRequest;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RatingDetailsDto {
	private int taste;
	private int ambience;
	private int kindness;
	private int cleanliness;

	public static RatingDetailsDto from(RatingDetails ratingDetails) {
		return RatingDetailsDto.builder()
			.taste(ratingDetails.getTaste())
			.ambience(ratingDetails.getAmbience())
			.kindness(ratingDetails.getKindness())
			.cleanliness(ratingDetails.getCleanliness())
			.build();
	}

	public static RatingDetailsDto from(RatingDetailsRequest ratingDetailsRequest) {
		return RatingDetailsDto.builder()
			.taste(ratingDetailsRequest.getTaste())
			.ambience(ratingDetailsRequest.getAmbience())
			.kindness(ratingDetailsRequest.getKindness())
			.cleanliness(ratingDetailsRequest.getCleanliness())
			.build();
	}
}
