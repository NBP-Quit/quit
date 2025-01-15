package com.quit.notification.application.service;

import org.springframework.stereotype.Service;

import com.quit.notification.infrastructure.messaging.message.ReservationEvent;
import com.quit.notification.infrastructure.messaging.message.ReservationMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

	private final UserService userService;
	private final StoreService storeService;

	@Override
	public void send(ReservationMessage message) {
		if (message.getReservationEvent() == ReservationEvent.CONFIRMED) {
			//TODO: 예약 확정 시 로직 작성
		}
	}
}
