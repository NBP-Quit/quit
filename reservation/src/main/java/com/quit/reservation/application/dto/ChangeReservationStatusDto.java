package com.quit.reservation.application.dto;

import com.quit.reservation.domain.enums.ReservationStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class ChangeReservationStatusDto {

    private ReservationStatus reservationStatus;

    public static ChangeReservationStatusDto of(ReservationStatus reservationStatus) {
        return ChangeReservationStatusDto.builder()
                .reservationStatus(reservationStatus)
                .build();
    }
}
