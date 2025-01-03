package com.quit.store.presentation.dto;

import com.quit.store.application.dto.StoreDto;
import com.quit.store.domain.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStoreRequest {

    private String name;
    private String description;
    private String address;
    private String contactNumber;
    private Integer reservationDeposit;
    private LocalTime openTime;
    private LocalTime closeTime;
    private LocalTime lastOrderTime;
    private Category category;

    public StoreDto toDto() {
        return StoreDto.of(
                this.name,
                this.description,
                this.address,
                this.contactNumber,
                this.reservationDeposit,
                this.openTime,
                this.closeTime,
                this.lastOrderTime,
                this.category
        );
    }

}
