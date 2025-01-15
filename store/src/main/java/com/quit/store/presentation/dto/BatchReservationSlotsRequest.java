package com.quit.store.presentation.dto;

import com.quit.store.application.dto.BatchReservationSlotsDto;
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
public class BatchReservationSlotsRequest {

    @NotNull(message = "RESERVATION_SLOT_START_DATE_EMPTY")
    private LocalDate startDate;

    @NotNull(message = "RESERVATION_SLOT_END_DATE_EMPTY")
    private LocalDate endDate;

    @NotNull(message = "RESERVATION_SLOT_START_TIME_EMPTY")
    private LocalTime startTime;

    @NotNull(message = "RESERVATION_SLOT_END_TIME_EMPTY")
    private LocalTime endTime;

    @NotNull(message = "RESERVATION_SLOT_INTERVAL_EMPTY")
    @Positive(message = "RESERVATION_SLOT_INTERVAL_INVALID")
    private Integer interval;

    @NotNull(message = "RESERVATION_SLOT_MAX_CAPACITY_EMPTY")
    @Positive(message = "RESERVATION_SLOT_MAX_CAPACITY_INVALID")
    private Integer maxCapacity;

    public BatchReservationSlotsDto toDto() {
        return BatchReservationSlotsDto.of(
                this.startDate,
                this.endDate,
                this.startTime,
                this.endTime,
                this.interval,
                this.maxCapacity
        );
    }

}
