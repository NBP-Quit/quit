package com.quit.reservation.infrastructure.messaging;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public interface MessageProducer {
    void sendReservationData(UUID slotId, Integer currentCapacity);
}
