package com.quit.queue.presentation.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.quit.queue.application.dto.ReservationDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class ReservationRequest {
    private Integer guestCount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reservationDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime reservationTime;

    public ReservationDto toDTO() {
        return ReservationDto.create(
                this.guestCount,
                this.reservationDate,
                this.reservationTime
        );
    }
}
