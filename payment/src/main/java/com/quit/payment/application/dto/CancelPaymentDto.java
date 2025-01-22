package com.quit.payment.application.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CancelPaymentDto {
    private String cancelReason;

    @Builder
    private CancelPaymentDto(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public static CancelPaymentDto from(String cancelReason) {
        return CancelPaymentDto.builder().cancelReason(cancelReason).build();
    }

}
