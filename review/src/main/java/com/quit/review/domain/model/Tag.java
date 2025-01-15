package com.quit.review.domain.model;

import lombok.Getter;

@Getter
public enum Tag {
	TASTE("맛"),
	AMBIENCE("분위기"),
	KINDNESS("친절"),
	CLEANLINESS("청결");

	private final String value;

	Tag(String value) {
		this.value = value;
	}
}
