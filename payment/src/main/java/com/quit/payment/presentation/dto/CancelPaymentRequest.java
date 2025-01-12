package com.quit.payment.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CancelPaymentRequest {

    @NotNull(message = "PAYMENT_CANCEL_REASON_EMPTY")
    private String cancelReason;

}
