package com.quit.payment.infrastructure.client;

import com.quit.payment.application.dto.PaymentDto;
import com.quit.payment.infrastructure.dto.CancelPaymentResponse;
import com.quit.payment.infrastructure.dto.ConfirmPaymentResponse;
import com.quit.payment.presentation.dto.CancelPaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentGatewayImpl implements PaymentGateway {

    private final PaymentClient paymentClient;

    @Override
    public ConfirmPaymentResponse confirmPayment(PaymentDto request) {
        return paymentClient.confirmPayment(request);
    }

    @Override
    public CancelPaymentResponse cancelPayment(String paymentKey, CancelPaymentRequest request) {
        return paymentClient.cancelPayment(paymentKey, request);
    }

}
