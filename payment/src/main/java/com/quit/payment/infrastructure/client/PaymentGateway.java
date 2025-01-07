package com.quit.payment.infrastructure.client;

import com.quit.payment.application.dto.PaymentDto;
import com.quit.payment.infrastructure.dto.CancelPaymentResponse;
import com.quit.payment.infrastructure.dto.ConfirmPaymentResponse;
import com.quit.payment.presentation.dto.CancelPaymentRequest;

public interface PaymentGateway {
    ConfirmPaymentResponse confirmPayment(PaymentDto request);
    CancelPaymentResponse cancelPayment(String paymentKey, CancelPaymentRequest request);
}
