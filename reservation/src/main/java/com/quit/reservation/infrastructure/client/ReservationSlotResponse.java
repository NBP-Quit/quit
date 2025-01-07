package com.quit.reservation.infrastructure.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ReservationSlotResponse {
    private UUID storeId;
    private UUID slotId;
    private LocalDate date;
    private LocalTime time;
    private Boolean isAvailable;
    private Integer maxCapacity;
    private Integer currentCapacity;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
