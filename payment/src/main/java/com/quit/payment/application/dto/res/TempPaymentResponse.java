package com.quit.payment.application.dto.res;

import com.quit.payment.domain.entity.TempPayment;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TempPaymentResponse {

    private UUID id;
    private String orderId;
    private Integer amount;

    @Builder
    private TempPaymentResponse(UUID id, String orderId, Integer amount) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
    }

    public static TempPaymentResponse from(TempPayment tempPayment) {
        return TempPaymentResponse.builder()
                .id(tempPayment.getId())
                .orderId(tempPayment.getOrderId())
                .amount(tempPayment.getAmount())
                .build();
    }

}
