package com.quit.reservation.infrastructure.messaging;

import com.quit.reservation.infrastructure.messaging.message.ReservationToStoreMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

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

    /* 메시지 전송: 예약 -> 가게-예약 슬롯 예약 생성 정보 전송(slotId, guestCount)*/
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

    private void sendMessage(String topic, String key, Object message) {
        kafkaTemplate.send(topic, key, message);
    }
}
