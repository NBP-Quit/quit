package com.quit.notification.infrastructure.messaging.message;

import lombok.Getter;

@Getter
public enum ReservationEvent {
	CONFIRMED,
	CANCELED
}
