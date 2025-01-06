package com.quit.reservation.application.dto;

import com.quit.reservation.domain.enums.ReservationStatus;
import com.quit.reservation.domain.model.Reservation;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class ChangeReservationStatusResponse implements Serializable {
    private UUID reservationId;
    private ReservationStatus reservationStatus;

    public static ChangeReservationStatusResponse fromReservation(Reservation reservation) {
        return ChangeReservationStatusResponse.builder()
                .reservationId(reservation.getReservationId())
                .reservationStatus(reservation.getReservationStatus())
                .build();
    }
}
