package com.quit.notification.application.service;

import com.quit.notification.infrastructure.messaging.message.ReservationMessage;

public interface SlackNotificationService {
	String sendDirectMessage(String slackId, String storeName,ReservationMessage reservationMessage);
}
