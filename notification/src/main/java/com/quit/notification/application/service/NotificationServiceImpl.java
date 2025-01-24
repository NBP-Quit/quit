package com.quit.notification.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.quit.notification.domain.model.Notification;
import com.quit.notification.domain.model.Status;
import com.quit.notification.domain.repository.NotificationRedisRepository;
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

	private final NotificationRedisRepository notificationRedisRepository;

	@Override
	public void notifyReservation(ReservationMessage reservationMessage) {
		String slackEmail = userService.getSlackEmail(reservationMessage.getCustomerId());
		String storeName = storeService.getName(reservationMessage.getStoreId());
		String message = null;
		try {
			message = slackNotificationService.sendDirectMessage(slackEmail, storeName, reservationMessage);
		} catch (Exception e) {
			Notification notification = Notification.create(reservationMessage.getReservationId(), e.getMessage(), Status.FAILURE,
				LocalDateTime.now());
			notificationRedisRepository.save(notification);
		}
		Notification notification = Notification.create(reservationMessage.getReservationId(), message, Status.SUCCESS,
			LocalDateTime.now());
		notificationRedisRepository.save(notification);
	}
}
