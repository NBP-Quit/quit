package com.quit.notification.infrastructure.messaging.message;

import lombok.Getter;

@Getter
public enum ReservationEvent {
	CONFIRMED("확정"),
	CANCELED("취소");

	private final String value;

	ReservationEvent(String value) {
		this.value = value;
	}
}
