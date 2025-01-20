package com.quit.notification.infrastructure.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.quit.notification.application.service.SlackNotificationService;
import com.quit.notification.infrastructure.messaging.message.ReservationEvent;
import com.quit.notification.infrastructure.messaging.message.ReservationMessage;

@SpringBootTest
class SlackNotificationServiceImplTest {

	private static final Logger log = LoggerFactory.getLogger(SlackNotificationServiceImplTest.class);
	@Autowired
	SlackNotificationService slackNotificationService;

	@Test
	@DisplayName("사용자의 슬랙 Email을 활용해 슬랙으로 DM을 보낸다")
	void sendDirectMessageBySlackEmail() {
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

		// when
		String message = slackNotificationService.sendDirectMessage(slackEmail, storeName, reservationMessage);

		// then
		log.info(message);
	}
}