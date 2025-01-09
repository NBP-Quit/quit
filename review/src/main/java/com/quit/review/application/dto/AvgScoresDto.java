package com.quit.review.application.dto;

import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AvgScoresDto {
	private double total;
	private double taste;
	private double ambience;
	private double kindness;
	private double cleanliness;

	public static AvgScoresDto from(Map<Object, Object> averages) {
		return AvgScoresDto.builder()
			.total(averages != null ? (double) averages.get("avg_total") : 0.0)
			.taste(averages != null ? (double) averages.get("avg_taste") : 0.0)
			.ambience(averages != null ? (double) averages.get("avg_ambience") : 0.0)
			.kindness(averages != null ? (double) averages.get("avg_kindness") : 0.0)
			.cleanliness(averages != null ? (double) averages.get("avg_cleanliness") : 0.0)
			.build();
	}
}
