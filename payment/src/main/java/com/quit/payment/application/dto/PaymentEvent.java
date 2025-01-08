package com.quit.payment.application.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentEvent {
    private UUID reservationId;
    private UUID paymentId;
    private Integer amount;

    @Builder
    private PaymentEvent(UUID reservationId, UUID paymentId, Integer amount) {
        this.reservationId = reservationId;
        this.paymentId = paymentId;
        this.amount = amount;
    }

    public static PaymentEvent of(UUID reservationId, UUID paymentId, Integer amount) {
        return PaymentEvent.builder()
                .reservationId(reservationId)
                .paymentId(paymentId)
                .amount(amount)
                .build();
    }

}
