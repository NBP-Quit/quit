package com.quit.reservation.presentation.request;

import com.quit.reservation.application.dto.CreateReservationDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationRequest implements Serializable {

    private UUID storeId;

    private Integer guestCount;

    private LocalDate reservationDate;

    private LocalTime reservationTime;

    public CreateReservationDto toDto() {
        return CreateReservationDto.of(this.storeId, this.guestCount,
                this.reservationDate, this.reservationTime);
    }
}
