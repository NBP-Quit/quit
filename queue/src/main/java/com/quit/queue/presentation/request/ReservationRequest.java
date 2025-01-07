package com.quit.queue.presentation.request;

import com.quit.queue.application.dto.ReservationDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class ReservationRequest {
    private Integer guestCount;
    private LocalDate reservationDate;
    private LocalTime reservationTime;

    public ReservationDto toDTO() {
        return ReservationDto.create(
                this.guestCount,
                this.reservationDate,
                this.reservationTime
        );
    }
}
