package com.quit.notification.infrastructure.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.quit.notification.application.service.NotificationService;
import com.quit.notification.infrastructure.messaging.message.ReservationMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageConsumer {

	private final NotificationService notificationService;

	@KafkaListener(groupId = "notification", topics = "reservation.notification")
	public void consume(ReservationMessage message) {
		log.info("consume reservation message: {}", message.toString());
		notificationService.send(message);
	}
}
