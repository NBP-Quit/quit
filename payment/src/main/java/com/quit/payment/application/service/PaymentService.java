package com.quit.payment.application.service;

import com.quit.payment.application.dto.TempPaymentDto;
import com.quit.payment.application.dto.res.TempPaymentResponse;
import com.quit.payment.domain.entity.TempPayment;
import com.quit.payment.domain.repository.PaymentRepository;
import com.quit.payment.domain.repository.TempPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TempPaymentRepository tempPaymentRepository;

    public TempPaymentResponse createTempPayment(TempPaymentDto request) {
        TempPayment tempPayment = TempPayment.of(request.getAmount(), request.getOrderId());
        tempPaymentRepository.save(tempPayment);
        return TempPaymentResponse.from(tempPayment);
    }


}
