package com.quit.payment.presentation.controller;

import com.quit.payment.application.dto.res.PaymentResponse;
import com.quit.payment.application.dto.res.TempPaymentResponse;
import com.quit.payment.application.service.PaymentService;
import com.quit.payment.common.dto.ApiResponse;
import com.quit.payment.presentation.dto.CancelPaymentRequest;
import com.quit.payment.presentation.dto.CreatePaymentRequest;
import com.quit.payment.presentation.dto.CreateTempPaymentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/temp")
    public ResponseEntity<ApiResponse<TempPaymentResponse>> createTempPayment(
            @Valid @RequestBody CreateTempPaymentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.createTempPayment(request.toDto())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @Valid @RequestBody CreatePaymentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.createPayment(request.toDto())));
    }

    @PostMapping("/{paymentsId}/cancel")
    public ResponseEntity<ApiResponse<PaymentResponse>> cancelPayment(
            @PathVariable UUID paymentsId,
            @Valid @RequestBody CancelPaymentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.cancelPayment(paymentsId, request)));
    }

}
