package com.quit.payment.presentation.dto;

import com.quit.payment.application.dto.TempPaymentDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTempPaymentRequest {

    @NotNull(message = "PAYMENT_ORDER_ID_EMPTY")
    private String orderId;

    @NotNull(message = "PAYMENT_AMOUNT_EMPTY")
    @Positive(message = "PAYMENT_AMOUNT_INVALID")
    private Integer amount;

    public TempPaymentDto toDto() {
        return TempPaymentDto.of(
                this.orderId,
                this.amount
        );
    }

}
