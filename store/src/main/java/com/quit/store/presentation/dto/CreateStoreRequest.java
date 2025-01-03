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

    @NotBlank(message = "STORE_NAME_EMPTY")
    private String name;

    private String description;

    @NotBlank(message = "STORE_ADDRESS_EMPTY")
    private String address;

    @NotBlank(message = "STORE_CONTACT_NUMBER_EMPTY")
    private String contactNumber;

    @NotNull(message = "STORE_RESERVATION_DEPOSIT_EMPTY")
    @Positive(message = "STORE_RESERVATION_DEPOSIT_INVALID")
    private Integer reservationDeposit;

    @NotNull(message = "STORE_OPEN_TIME_EMPTY")
    private LocalTime openTime;

    @NotNull(message = "STORE_CLOSE_TIME_EMPTY")
    private LocalTime closeTime;

    @NotNull(message = "STORE_LAST_ORDER_TIME_EMPTY")
    private LocalTime lastOrderTime;

    @NotNull(message = "STORE_CATEGORY_EMPTY")
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
