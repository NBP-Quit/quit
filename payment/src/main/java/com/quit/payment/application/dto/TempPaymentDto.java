package com.quit.payment.application.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TempPaymentDto {

    private String orderId;
    private Integer amount;

    @Builder
    private TempPaymentDto(String orderId, Integer amount) {
        this.orderId = orderId;
        this.amount = amount;
    }

    public static TempPaymentDto of(String orderId, Integer amount) {
        return TempPaymentDto.builder()
                .orderId(orderId)
                .amount(amount)
                .build();
    }

}
