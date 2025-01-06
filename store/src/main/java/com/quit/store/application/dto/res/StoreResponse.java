package com.quit.store.application.dto.res;

import com.quit.store.domain.entity.Category;
import com.quit.store.domain.entity.Store;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreResponse {
    private UUID id;
    private String name;
    private String description;
    private String address;
    private String contactNumber;
    private Integer reservationDeposit;
    private LocalTime openTime;
    private LocalTime closeTime;
    private LocalTime lastOrderTime;
    private Category category;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    @Builder
    private StoreResponse(UUID id, String name, String description, String address,
                          String contactNumber, Integer reservationDeposit,
                          LocalTime openTime, LocalTime closeTime,
                          LocalTime lastOrderTime, Category category,
                          LocalDateTime createdAt, String createdBy,
                          LocalDateTime updatedAt, String updatedBy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.contactNumber = contactNumber;
        this.reservationDeposit = reservationDeposit;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.lastOrderTime = lastOrderTime;
        this.category = category;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public static StoreResponse from(Store store) {
        return StoreResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .description(store.getDescription())
                .address(store.getAddress())
                .contactNumber(store.getContactNumber())
                .reservationDeposit(store.getReservationDeposit())
                .openTime(store.getOpenTime())
                .closeTime(store.getCloseTime())
                .lastOrderTime(store.getLastOrderTime())
                .category(store.getCategory())
                .createdAt(store.getCreatedAt())
                .createdBy(store.getCreatedBy())
                .updatedAt(store.getUpdatedAt())
                .updatedBy(store.getUpdatedBy())
                .build();
    }

}
