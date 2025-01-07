package com.quit.queue.application.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class ReservationDto {
    private Integer guestCount;
    private LocalDate reservationDate;
    private LocalTime reservationTime;

    public static ReservationDto create(
            Integer guestCount,
            LocalDate reservationDate,
            LocalTime reservationTime
    ) {
        return ReservationDto.builder()
                .guestCount(guestCount)
                .reservationDate(reservationDate)
                .reservationTime(reservationTime)
                .build();
    }
}
