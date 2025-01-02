package com.quit.store.presentation.dto;

import com.quit.store.application.dto.StoreDto;
import com.quit.store.domain.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateStoreRequest {

    //todo: 필드별 validation message 추가

    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String address;

    @NotBlank
    private String contactNumber;

    @NotNull
    @Positive
    private Integer reservationDeposit;

    @NotNull
    private LocalTime openTime;

    @NotNull
    private LocalTime closeTime;

    @NotNull
    private LocalTime lastOrderTime;

    @NotNull
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
