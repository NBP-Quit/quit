package com.quit.review.application.dto;

import com.quit.review.domain.model.Scores;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ScoresDto {
	private int taste;
	private int ambience;
	private int kindness;
	private int cleanliness;

	public static ScoresDto from(Scores scores) {
		return ScoresDto.builder()
			.taste(scores.getTaste())
			.ambience(scores.getAmbience())
			.kindness(scores.getKindness())
			.cleanliness(scores.getCleanliness())
			.build();
	}
}
