package com.quit.store.infrastructure.kafka;

import com.quit.store.application.dto.ReservationEvent;
import com.quit.store.application.service.ReservationSlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageListener {

    private final ReservationSlotService reservationSlotService;

    @KafkaListener(topics = "reservation.confirm.success", groupId = "reservation-slot", containerFactory = "kafkaReservationEventContainerFactory")
    public void handleReservationSuccess(ReservationEvent reservationEvent) {
        log.info("Consumed reservation.confirm.success event: {}", reservationEvent.getReservationSlotId());
        UUID reservationSlotId = reservationEvent.getReservationSlotId();
        Integer currentCapacity = reservationEvent.getCurrentCapacity();
        reservationSlotService.increaseCapacity(reservationSlotId, currentCapacity);
    }

    @KafkaListener(topics = "reservation.confirm.failed", groupId = "reservation-slot", containerFactory = "kafkaReservationEventContainerFactory")
    public void handleReservationFailure(ReservationEvent reservationEvent) {
        log.info("Consumed reservation.confirm.failed event: {}", reservationEvent.getReservationSlotId());
        UUID reservationSlotId = reservationEvent.getReservationSlotId();
        Integer currentCapacity = reservationEvent.getCurrentCapacity();
        reservationSlotService.restoreCapacity(reservationSlotId, currentCapacity);
    }

}
