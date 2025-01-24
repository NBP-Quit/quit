package com.quit.notification.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.quit.notification.infrastructure.messaging.message.ReservationEvent;
import com.quit.notification.infrastructure.messaging.message.ReservationMessage;

@SpringBootTest
class NotificationServiceImplTest {

	@Autowired
	private NotificationServiceImpl notificationService;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private StoreService storeService;

	@Test
	@DisplayName("슬랙 알림 발송을 성공하면 내요이 저장된다.")
	void sendDMAndSaveContent() {
	    // given
		String slackEmail = "geon4735@gmail.com";
		String storeName = "맥도날드";
		ReservationMessage reservationMessage = ReservationMessage.builder()
			.reservationId(UUID.randomUUID())
			.storeId(UUID.randomUUID())
			.customerId("geon4735@gmail.com")
			.guestCount(2)
			.reservationDate(LocalDate.now())
			.reservationTime(LocalTime.now())
			.reservationPrice(10000)
			.reservationEvent(ReservationEvent.CONFIRMED)
			.build();

		given(userService.getSlackEmail(any(String.class))).willReturn(slackEmail);
		given(storeService.getName(any(UUID.class))).willReturn(storeName);

	    // when
		notificationService.notifyReservation(reservationMessage);

	    // then
	}
}