package com.quit.reservation.application.dto;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class CreateReservationResponse implements Serializable {
    private UUID reservationId;

    public static CreateReservationResponse of(UUID reservationId) {
        return CreateReservationResponse.builder()
                .reservationId(reservationId)
                .build();
    }
}
