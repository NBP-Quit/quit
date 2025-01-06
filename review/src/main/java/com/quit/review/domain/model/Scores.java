package com.quit.review.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Scores {

	@Column(name = "taste_score", nullable = false)
	int taste;

	@Column(name = "ambience_score", nullable = false)
	int ambience;

	@Column(name = "kindness_score", nullable = false)
	int kindness;

	@Column(name = "cleanliness_score", nullable = false)
	int cleanliness;

	@Builder
	private Scores(int taste, int ambience, int kindness, int cleanliness) {
		this.taste = taste;
		this.ambience = ambience;
		this.kindness = kindness;
		this.cleanliness = cleanliness;
	}

	public double calculateAverage() {
		return (taste + ambience + kindness + cleanliness) / 4.0;
	}

	public void update(int taste, int ambience, int kindness, int cleanliness) {
		this.taste = taste;
		this.ambience = ambience;
		this.kindness = kindness;
		this.cleanliness = cleanliness;
	}
}
