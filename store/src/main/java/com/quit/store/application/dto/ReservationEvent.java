package com.quit.store.application.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class ReservationEvent {
    private UUID reservationSlotId;
    private Integer currentCapacity;
}
