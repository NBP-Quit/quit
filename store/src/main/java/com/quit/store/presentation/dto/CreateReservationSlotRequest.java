package com.quit.store.presentation.dto;

import com.quit.store.application.dto.ReservationSlotDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationSlotRequest {

    @NotNull(message = "RESERVATION_SLOT_DATE_EMPTY")
    private LocalDate date;

    @NotNull(message = "RESERVATION_SLOT_TIME_EMPTY")
    private LocalTime time;

    @NotNull(message = "RESERVATION_SLOT_MAX_CAPACITY_EMPTY")
    @Positive(message = "RESERVATION_SLOT_MAX_CAPACITY_INVALID")
    private Integer maxCapacity;

    public ReservationSlotDto toDto() {
        return ReservationSlotDto.of(
                this.date,
                this.time,
                this.maxCapacity
        );
    }

}
