package com.quit.reservation.infrastructure.messaging.message;

import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class ReservationToStoreMessage {
    private UUID slotId;
    private Integer currentCapacity;

    public static ReservationToStoreMessage of(UUID slotId, Integer currentCapacity) {
        return ReservationToStoreMessage.builder()
                .slotId(slotId)
                .currentCapacity(currentCapacity)
                .build();
    }
}
