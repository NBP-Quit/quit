package com.quit.reservation.infrastructure.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class GetReservationSlotResponse {
    private UUID storeId;
    private UUID slotId;
    private Boolean isAvailable;
    private Integer maxCapacity;
    private Integer currentCapacity;
}
