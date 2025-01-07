package com.quit.payment.infrastructure.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ConfirmPaymentResponse {
    private String paymentKey;
    private String orderId;
    private String status;
    private Integer totalAmount;
}
