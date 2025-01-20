package com.quit.notification.application.service;

import org.springframework.stereotype.Service;

import com.quit.notification.infrastructure.messaging.message.ReservationMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

	private final UserService userService;
	private final StoreService storeService;

	private final SlackNotificationService slackNotificationService;

	@Override
	public void send(ReservationMessage reservationMessage) {
		String slackEmail = userService.getSlackEmail(reservationMessage.getCustomerId());
		String storeName = storeService.getName(reservationMessage.getStoreId());
		slackNotificationService.sendDirectMessage(slackEmail, storeName, reservationMessage);
	}
}
