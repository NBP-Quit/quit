package com.quit.reservation.infrastructure.messaging;

import com.quit.reservation.domain.enums.ReservationStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Component
public interface MessageProducer {
    void sendReservationData(UUID slotId, Integer currentCapacity);
    void sendReservationFailed(UUID slotId, Integer currentCapacity);
    void sendReservationNotification(UUID reservationId, String customerId,
                                     UUID storeId, Integer guestCount,
                                     LocalDate reservationDate, LocalTime reservationTime,
                                     ReservationStatus reservationStatus, Integer reservationPrice);
}
