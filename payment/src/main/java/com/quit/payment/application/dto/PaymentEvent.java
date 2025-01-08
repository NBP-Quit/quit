package com.quit.payment.application.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentEvent {
    private UUID paymentId;
    private Integer amount;

    @Builder
    private PaymentEvent(UUID paymentId, Integer amount) {
        this.paymentId = paymentId;
        this.amount = amount;
    }

    public static PaymentEvent of(UUID paymentId, Integer amount) {
        return PaymentEvent.builder()
                .paymentId(paymentId)
                .amount(amount)
                .build();
    }

}
