package com.quit.payment.presentation.dto;

import com.quit.payment.application.dto.TempPaymentDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTempPaymentRequest {

    @NotNull
    private String orderId;

    @NotNull
    private Integer amount;

    public TempPaymentDto toDto() {
        return TempPaymentDto.of(
                this.orderId,
                this.amount
        );
    }

}
