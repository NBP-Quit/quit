package com.quit.store.application.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateReservationSlotDto {

    private LocalDate date;
    private LocalTime time;
    private Integer maxCapacity;
    private Boolean isAvailable;

    @Builder
    private UpdateReservationSlotDto(LocalDate date, LocalTime time,
                                     Integer maxCapacity, Boolean isAvailable) {
        this.date = date;
        this.time = time;
        this.maxCapacity = maxCapacity;
        this.isAvailable = isAvailable;
    }

    public static UpdateReservationSlotDto of(LocalDate date, LocalTime time,
                                              Integer maxCapacity, Boolean isAvailable) {
        return UpdateReservationSlotDto.builder()
                .date(date)
                .time(time)
                .maxCapacity(maxCapacity)
                .isAvailable(isAvailable)
                .build();

    }

}
