package com.quit.payment.infrastructure.dto;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@ToString
public class ReservationResponse {
    private UUID reservationId;
    private String customerId;
    private UUID storeId;
    private Integer guestCount;
    private LocalDate reservationDate;
    private LocalTime reservationTime;
    private String reservationStatus;
    private Integer reservationPrice;
}
