package com.quit.store.application.dto.res;

import com.quit.store.domain.entity.ReservationSlot;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Builder
    private ReservationSlotResponse(UUID storeId, UUID slotId,
                                    LocalDate date, LocalTime time, Boolean isAvailable,
                                   Integer maxCapacity, Integer currentCapacity,
                                   LocalDateTime createdAt, String createdBy,
                                   LocalDateTime updatedAt, String updatedBy) {
        this.storeId = storeId;
        this.slotId = slotId;
        this.date = date;
        this.time = time;
        this.isAvailable = isAvailable;
        this.maxCapacity = maxCapacity;
        this.currentCapacity = currentCapacity;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public static ReservationSlotResponse from(ReservationSlot reservationSlot) {
        return ReservationSlotResponse.builder()
                .storeId(reservationSlot.getStore().getId())
                .slotId(reservationSlot.getId())
                .date(reservationSlot.getDate())
                .time(reservationSlot.getTime())
                .isAvailable(reservationSlot.getIsAvailable())
                .maxCapacity(reservationSlot.getMaxCapacity())
                .currentCapacity(reservationSlot.getCurrentCapacity())
                .createdAt(reservationSlot.getCreatedAt())
                .createdBy(reservationSlot.getCreatedBy())
                .updatedAt(reservationSlot.getUpdatedAt())
                .updatedBy(reservationSlot.getUpdatedBy())
                .build();
    }

}
