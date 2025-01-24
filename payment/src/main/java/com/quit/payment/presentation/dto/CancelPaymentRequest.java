package com.quit.payment.presentation.dto;

import com.quit.payment.application.dto.CancelPaymentDto;
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

    @NotNull(message = "PAYMENT_IDEMPOTENCY_KEY_EMPTY")
    private String idempotencyKey;

    public CancelPaymentDto toDto() {
        return CancelPaymentDto.from(this.cancelReason);
    }

}
