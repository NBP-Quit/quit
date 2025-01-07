package com.quit.review.presentation.request;

import com.quit.review.application.dto.ReviewCreateDto;
import com.quit.review.application.dto.ReviewUpdateDto;
import com.quit.review.application.dto.ScoresDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReviewUpdateRequest {

	@NotBlank
	@Size(min = 10, max = 1000)
	private String content;

	@NotNull
	@Valid
	private ScoresRequest scores;

	public ReviewUpdateDto toDto() {
		return ReviewUpdateDto.builder()
			.content(content)
			.scores(
				ScoresDto.builder()
					.taste(scores.getTaste())
					.ambience(scores.getAmbience())
					.cleanliness(scores.getCleanliness())
					.kindness(scores.getKindness())
					.build()
			)
			.build();
	}
}
