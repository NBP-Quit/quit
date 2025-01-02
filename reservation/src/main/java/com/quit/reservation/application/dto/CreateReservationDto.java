package com.quit.reservation.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class CreateReservationDto implements Serializable {

    private UUID storeId;

    private Integer guestCount;

    private LocalDate reservationDate;

    private LocalTime reservationTime;

    public static CreateReservationDto of(UUID storeId, Integer guestCount,
                                          LocalDate reservationDate, LocalTime reservationTime) {
        return CreateReservationDto.builder()
                .storeId(storeId)
                .guestCount(guestCount)
                .reservationDate(reservationDate)
                .reservationTime(reservationTime)
                .build();
    }
}
