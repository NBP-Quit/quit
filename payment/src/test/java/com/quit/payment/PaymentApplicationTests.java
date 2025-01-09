package com.quit.payment;

import com.quit.payment.application.dto.ReservationEvent;
import com.quit.payment.infrastructure.kafka.KafkaProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class PaymentApplicationTests {

	@Autowired
	private KafkaProducer kafkaProducer;

	@Test
	void testSendSuccessMessage() {
		// 메시지 생성
		ReservationEvent message = new ReservationEvent();
		UUID uuid = UUID.fromString("972a1e2c-a2b1-4704-96c7-63cddbddbb41");
		message.setReservationSlotId(uuid);
		message.setCurrentCapacity(2);

		// Kafka로 메시지 전송
		kafkaProducer.sendMessage("reservation.confirm.success", message.getReservationSlotId().toString(), message);
		System.out.println("Message sent to Kafka: " + message);
	}

	@Test
	void testSendFailMessage() {
		// 메시지 생성
		ReservationEvent message = new ReservationEvent();
		UUID uuid = UUID.fromString("972a1e2c-a2b1-4704-96c7-63cddbddbb41");
		message.setReservationSlotId(uuid);
		message.setCurrentCapacity(2);

		// Kafka로 메시지 전송
		kafkaProducer.sendMessage("reservation.confirm.failed", message.getReservationSlotId().toString(), message);
		System.out.println("Message sent to Kafka: " + message);
	}

}
