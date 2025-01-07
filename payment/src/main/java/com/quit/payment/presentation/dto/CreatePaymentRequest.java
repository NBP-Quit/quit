package com.quit.payment.presentation.dto;

import com.quit.payment.application.dto.PaymentDto;
import com.quit.payment.application.dto.TempPaymentDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {

    @NotNull
    private String orderId;

    @NotNull
    private Integer amount;

    @NotNull
    private String paymentKey;

    public PaymentDto toDto() {
        return PaymentDto.of(
                this.orderId,
                this.amount,
                this.paymentKey
        );
    }

}
