package com.quit.store.application.dto.res;

import com.quit.store.domain.entity.ReservationSlot;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetReservationSlotResponse {
    private UUID storeId;
    private UUID slotId;
    private Boolean isAvailable;
    private Integer maxCapacity;
    private Integer currentCapacity;

    @Builder
    private GetReservationSlotResponse(UUID storeId, UUID slotId, Boolean isAvailable,
                                       Integer maxCapacity, Integer currentCapacity) {
        this.storeId = storeId;
        this.slotId = slotId;
        this.isAvailable = isAvailable;
        this.maxCapacity = maxCapacity;
        this.currentCapacity = currentCapacity;
    }

    public static GetReservationSlotResponse from(ReservationSlot reservationSlot) {
        return GetReservationSlotResponse.builder()
                .storeId(reservationSlot.getStore().getId())
                .slotId(reservationSlot.getId())
                .isAvailable(reservationSlot.getIsAvailable())
                .maxCapacity(reservationSlot.getMaxCapacity())
                .currentCapacity(reservationSlot.getCurrentCapacity())
                .build();
    }

}
