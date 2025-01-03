package com.quit.reservation.application.dto;

import com.quit.reservation.domain.enums.ReservationStatus;
import com.quit.reservation.domain.model.Reservation;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class UpdateReservationResponse implements Serializable {
    private UUID reservationId;
    private String customerId;
    private UUID storeId;
    private Integer guestCount;
    private LocalDate reservationDate;
    private LocalTime reservationTime;
    private ReservationStatus reservationStatus;

    public static UpdateReservationResponse fromReservation(Reservation reservation) {
        return UpdateReservationResponse.builder()
                .reservationId(reservation.getReservationId())
                .customerId(reservation.getCustomerId())
                .storeId(reservation.getStoreId())
                .guestCount(reservation.getGuestCount())
                .reservationDate(reservation.getReservationDate())
                .reservationTime(reservation.getReservationTime())
                .reservationStatus(reservation.getReservationStatus())
                .build();
    }
}
