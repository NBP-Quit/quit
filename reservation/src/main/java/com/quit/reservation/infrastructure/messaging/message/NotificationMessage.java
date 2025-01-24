package com.quit.reservation.infrastructure.messaging.message;

import com.quit.reservation.domain.enums.ReservationStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class NotificationMessage {

    private UUID reservationId;
    private String customerId;
    private UUID storeId;
    private Integer guestCount;
    private LocalDate reservationDate;
    private LocalTime reservationTime;
    private ReservationStatus reservationStatus;
    private Integer reservationPrice;

    public static NotificationMessage of(
            UUID reservationId, String customerId, UUID storeId,
            Integer guestCount, LocalDate reservationDate, LocalTime reservationTime,
            ReservationStatus reservationStatus, Integer reservationPrice
    ) {
        return NotificationMessage.builder()
                .reservationId(reservationId)
                .customerId(customerId)
                .storeId(storeId)
                .guestCount(guestCount)
                .reservationDate(reservationDate)
                .reservationTime(reservationTime)
                .reservationStatus(reservationStatus)
                .reservationPrice(reservationPrice)
                .build();
    }
}
