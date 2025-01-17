package com.quit.reservation.infrastructure.messaging;

import com.quit.reservation.domain.enums.ReservationStatus;
import com.quit.reservation.infrastructure.messaging.message.NotificationMessage;
import com.quit.reservation.infrastructure.messaging.message.ReservationToStoreMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Component
public class ReservationMessagingProducer implements MessageProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ReservationMessagingProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    //TODO: value 값 환경변수 처리 고려
    @Value("reservation.confirm.success")
    private String reservationTopic;

    @Value("reservation.confirm.failed")
    private String reservationFailedTopic;

    @Value("reservation.notification")
    private String reservationNotificationTopic;

    @Override
    public void sendReservationData(UUID slotId, Integer currentCapacity) {
        ReservationToStoreMessage message = ReservationToStoreMessage.of(slotId, currentCapacity);
        sendMessage(reservationTopic, slotId.toString(), message);
    }

    @Override
    public void sendReservationFailed(UUID slotId, Integer currentCapacity) {
        ReservationToStoreMessage message = ReservationToStoreMessage.of(slotId, currentCapacity);
        sendMessage(reservationFailedTopic, slotId.toString(), message);
    }

    @Override
    public void sendReservationNotification(UUID reservationId, String customerId, UUID storeId,
                                            Integer guestCount, LocalDate reservationDate, LocalTime reservationTime,
                                            ReservationStatus reservationStatus, Integer reservationPrice) {
        NotificationMessage message = NotificationMessage.of(reservationId, customerId, storeId,
                guestCount, reservationDate, reservationTime, reservationStatus, reservationPrice);
        sendMessage(reservationNotificationTopic, storeId.toString(), message);
    }

    private void sendMessage(String topic, String key, Object message) {
        kafkaTemplate.send(topic, key, message);
    }
}
