package com.quit.review.presentation.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScoresRequest {

	@Min(1) @Max(5)
	private int taste;

	@Min(1) @Max(5)
	private int ambience;

	@Min(1) @Max(5)
	private int kindness;

	@Min(1) @Max(5)
	private int cleanliness;
}
