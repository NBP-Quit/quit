package com.quit.reservation.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class UpdateReservationDto implements Serializable {

    private Integer guestCount;

    private LocalDate reservationDate;

    private LocalTime reservationTime;

    public static UpdateReservationDto of(Integer guestCount,
                                          LocalDate reservationDate,
                                          LocalTime reservationTime) {
        return UpdateReservationDto.builder()
                .guestCount(guestCount)
                .reservationDate(reservationDate)
                .reservationTime(reservationTime)
                .build();
    }
}
