package com.quit.payment.infrastructure.client;

import com.quit.payment.application.dto.CancelPaymentDto;
import com.quit.payment.application.dto.PaymentDto;
import com.quit.payment.infrastructure.dto.CancelPaymentResponse;
import com.quit.payment.infrastructure.dto.ConfirmPaymentResponse;

public interface PaymentGateway {
    ConfirmPaymentResponse confirmPayment(String idempotencyKey, PaymentDto request);
    CancelPaymentResponse cancelPayment(String idempotencyKey, String paymentKey, CancelPaymentDto request);
}
