package com.quit.payment.presentation.controller;

import com.quit.payment.application.dto.res.TempPaymentResponse;
import com.quit.payment.application.service.PaymentService;
import com.quit.payment.common.dto.ApiResponse;
import com.quit.payment.presentation.dto.CreateTempPaymentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("//api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/temp")
    public ResponseEntity<ApiResponse<TempPaymentResponse>> createTempPayment(
            @Valid @RequestBody CreateTempPaymentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.createTempPayment(request.toDto())));
    }

}
