package com.quit.notification.application.service;

import java.io.IOException;

import com.quit.notification.infrastructure.messaging.message.ReservationMessage;
import com.slack.api.methods.SlackApiException;

public interface SlackNotificationService {
	String sendDirectMessage(String slackId, String storeName,ReservationMessage reservationMessage) throws
		SlackApiException,
		IOException;
}
