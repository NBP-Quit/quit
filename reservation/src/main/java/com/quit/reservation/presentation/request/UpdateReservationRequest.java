package com.quit.reservation.presentation.request;

import com.quit.reservation.application.dto.UpdateReservationDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class UpdateReservationRequest {

    private Integer guestCount;

    private LocalDate reservationDate;

    private LocalTime reservationTime;

    public UpdateReservationDto toDto() {
        return UpdateReservationDto.of(this.guestCount,
                this.reservationDate, this.reservationTime);
    }
}
