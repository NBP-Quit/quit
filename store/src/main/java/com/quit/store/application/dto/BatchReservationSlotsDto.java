package com.quit.store.application.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BatchReservationSlotsDto {

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer interval;
    private Integer maxCapacity;

    @Builder
    private BatchReservationSlotsDto(LocalDate startDate, LocalDate endDate,
                                    LocalTime startTime, LocalTime endTime,
                                    Integer interval, Integer maxCapacity) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.interval = interval;
        this.maxCapacity = maxCapacity;
    }

    public static BatchReservationSlotsDto of(LocalDate startDate, LocalDate endDate,
                                              LocalTime startTime, LocalTime endTime,
                                              Integer interval, Integer maxCapacity) {
        return BatchReservationSlotsDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .startTime(startTime)
                .endTime(endTime)
                .interval(interval)
                .maxCapacity(maxCapacity)
                .build();
    }

}
