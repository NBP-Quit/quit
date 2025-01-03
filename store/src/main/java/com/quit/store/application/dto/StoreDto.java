package com.quit.store.application.dto;

import com.quit.store.domain.entity.Category;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreDto {

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
    private StoreDto(String name, String description, String address,
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

    public static StoreDto of(String name, String description, String address,
                              String contactNumber, Integer reservationDeposit,
                              LocalTime openTime, LocalTime closeTime,
                              LocalTime lastOrderTime, Category category) {
        return StoreDto.builder()
                .name(name)
                .description(description)
                .address(address)
                .contactNumber(contactNumber)
                .reservationDeposit(reservationDeposit)
                .openTime(openTime)
                .closeTime(closeTime)
                .lastOrderTime(lastOrderTime)
                .category(category)
                .build();
    }

}
