package com.quit.payment.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CancelPaymentResponse {
    private String paymentKey;
    private String orderId;
    private String status;
}
