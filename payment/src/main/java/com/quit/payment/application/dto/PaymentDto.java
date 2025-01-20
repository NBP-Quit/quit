package com.quit.payment.application.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentDto {

    private String orderId;
    private Integer amount;
    private String paymentKey;
    private String idempotencyKey;

    @Builder
    private PaymentDto(String orderId, Integer amount,
                       String paymentKey, String idempotencyKey) {
        this.orderId = orderId;
        this.amount = amount;
        this.paymentKey = paymentKey;
        this.idempotencyKey = idempotencyKey;
    }

    public static PaymentDto of(String orderId, Integer amount, String paymentKey, String idempotencyKey) {
        return PaymentDto.builder()
                .orderId(orderId)
                .amount(amount)
                .paymentKey(paymentKey)
                .idempotencyKey(idempotencyKey)
                .build();
    }

}
