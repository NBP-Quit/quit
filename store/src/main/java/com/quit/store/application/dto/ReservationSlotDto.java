package com.quit.store.application.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationSlotDto {

    private LocalDate date;
    private LocalTime time;
    private Integer maxCapacity;

    @Builder
    private ReservationSlotDto(LocalDate date, LocalTime time, Integer maxCapacity) {
        this.date = date;
        this.time = time;
        this.maxCapacity = maxCapacity;
    }

    public static ReservationSlotDto of(LocalDate date, LocalTime time, Integer maxCapacity) {
        return ReservationSlotDto.builder()
                .date(date)
                .time(time)
                .maxCapacity(maxCapacity)
                .build();

    }

}
