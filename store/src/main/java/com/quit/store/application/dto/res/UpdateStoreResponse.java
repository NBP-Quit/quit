package com.quit.store.application.dto.res;

import com.quit.store.domain.entity.Category;
import com.quit.store.domain.entity.Store;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateStoreResponse {

    private String name;
    private String description;
    private String address;
    private String contactNumber;
    private Integer reservationDeposit;
    private LocalTime openTime;
    private LocalTime closeTime;
    private LocalTime lastOrderTime;
    private Category category;

    @Builder
    private UpdateStoreResponse(String name, String description, String address,
                               String contactNumber, Integer reservationDeposit,
                               LocalTime openTime, LocalTime closeTime,
                               LocalTime lastOrderTime, Category category) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.contactNumber = contactNumber;
        this.reservationDeposit = reservationDeposit;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.lastOrderTime = lastOrderTime;
        this.category = category;
    }

    public static UpdateStoreResponse from(Store store) {
        return UpdateStoreResponse.builder()
                .name(store.getName())
                .description(store.getDescription())
                .address(store.getAddress())
                .contactNumber(store.getContactNumber())
                .reservationDeposit(store.getReservationDeposit())
                .openTime(store.getOpenTime())
                .closeTime(store.getCloseTime())
                .lastOrderTime(store.getLastOrderTime())
                .category(store.getCategory())
                .build();
    }

}
