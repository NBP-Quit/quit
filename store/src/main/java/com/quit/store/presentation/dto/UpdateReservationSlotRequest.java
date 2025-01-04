package com.quit.store.presentation.dto;

import com.quit.store.application.dto.ReservationSlotDto;
import com.quit.store.application.dto.UpdateReservationSlotDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReservationSlotRequest {

    private LocalDate date;
    private LocalTime time;
    private Integer maxCapacity;
    private Boolean isAvailable;

    public UpdateReservationSlotDto toDto() {
        return UpdateReservationSlotDto.of(
                this.date,
                this.time,
                this.maxCapacity,
                this.isAvailable
        );
    }

}
