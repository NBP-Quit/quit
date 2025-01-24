package com.quit.notification.application.service;

import com.quit.notification.infrastructure.messaging.message.ReservationMessage;

public interface NotificationService {
	void notifyReservation(ReservationMessage message);
}
