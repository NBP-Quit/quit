package com.quit.store.presentation.dto;

import com.quit.store.application.dto.ReservationSlotDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationSlotRequest {

    private LocalDate date;
    private LocalTime time;
    private Integer maxCapacity;

    public ReservationSlotDto toDto() {
        return ReservationSlotDto.of(
                this.date,
                this.time,
                this.maxCapacity
        );
    }

}
