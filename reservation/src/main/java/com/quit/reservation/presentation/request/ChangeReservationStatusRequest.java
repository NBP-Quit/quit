package com.quit.reservation.presentation.request;

import com.quit.reservation.application.dto.ChangeReservationStatusDto;
import com.quit.reservation.domain.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeReservationStatusRequest {

    private ReservationStatus reservationStatus;

    public ChangeReservationStatusDto toDto() {
        return ChangeReservationStatusDto.of(this.reservationStatus);
    }
}
