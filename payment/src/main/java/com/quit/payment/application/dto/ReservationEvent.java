package com.quit.payment.application.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class ReservationEvent {
    private UUID reservationSlotId;
    private Integer currentCapacity;
}
