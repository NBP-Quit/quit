package com.quit.payment.application.service;

import com.quit.payment.application.dto.PaymentDto;
import com.quit.payment.application.dto.TempPaymentDto;
import com.quit.payment.application.dto.res.PaymentResponse;
import com.quit.payment.application.dto.res.TempPaymentResponse;
import com.quit.payment.domain.entity.Payment;
import com.quit.payment.domain.entity.Status;
import com.quit.payment.domain.entity.TempPayment;
import com.quit.payment.domain.repository.PaymentRepository;
import com.quit.payment.domain.repository.TempPaymentRepository;
import com.quit.payment.infrastructure.client.PaymentClient;
import com.quit.payment.infrastructure.client.PaymentGateway;
import com.quit.payment.infrastructure.dto.CancelPaymentResponse;
import com.quit.payment.infrastructure.dto.ConfirmPaymentResponse;
import com.quit.payment.presentation.dto.CancelPaymentRequest;
import com.quit.payment.presentation.exception.CustomException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.quit.payment.presentation.exception.ErrorType.PAYMENT_DATA_INVALID;
import static com.quit.payment.presentation.exception.ErrorType.PAYMENT_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TempPaymentRepository tempPaymentRepository;
    private final PaymentGateway paymentGateway;

    public TempPaymentResponse createTempPayment(TempPaymentDto request) {
        TempPayment tempPayment = TempPayment.of(request.getAmount(), request.getOrderId());
        tempPaymentRepository.save(tempPayment);
        return TempPaymentResponse.from(tempPayment);
    }

    public PaymentResponse createPayment(PaymentDto request) {
        TempPayment tempPayment = ValidatePayment(request.getOrderId());
        validateAmount(tempPayment, request.getAmount());
        ConfirmPaymentResponse response = paymentGateway.confirmPayment(request);
        log.info("Confirm payment response: {}", response);
        /* todo:
            1. kafka 적용 후 예약 생성 구독해 예약 id 가져오기
            2. 결제 생성 후 메세지 발행하기
         */
        UUID reservationId = UUID.randomUUID();
        Payment payment = create(response, reservationId);
        paymentRepository.save(payment);
        return PaymentResponse.from(payment);
    }

    public PaymentResponse cancelPayment(UUID paymentsId, CancelPaymentRequest request) {
        Payment payment = checkPayment(paymentsId);
        CancelPaymentResponse response = paymentGateway.cancelPayment(payment.getPaymentKey(), request);
        log.info("Cancel payment response: {}", response);
        /* todo:
            1. kafka 적용 결제 취소 후 메세지 발행하기
         */
        payment.cancel(Status.CANCELED, request.getCancelReason());
        return PaymentResponse.from(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByReservation(UUID reservationId) {
        Payment payment = paymentRepository.findByReservationIdAndIsDeletedFalse(reservationId)
                .orElseThrow(() -> new CustomException(PAYMENT_NOT_FOUND));
        return PaymentResponse.from(payment);
    }

    private Payment checkPayment(UUID paymentsId) {
        return paymentRepository.findByIdAndIsDeletedFalse(paymentsId).orElseThrow(() -> new CustomException(PAYMENT_NOT_FOUND));
    }

    private Payment create(ConfirmPaymentResponse response, UUID reservationId) {
        return Payment.of(
                response.getTotalAmount(),
                Status.SUCCESS,
                response.getPaymentKey(),
                response.getOrderId(),
                reservationId);
    }

    private void validateAmount(TempPayment tempPayment, Integer amount) {
        if(!tempPayment.getAmount().equals(amount)) {
            throw new CustomException(PAYMENT_DATA_INVALID);
        }
    }

    private TempPayment ValidatePayment(String orderId) {
        return tempPaymentRepository.findByOrderIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new CustomException(PAYMENT_DATA_INVALID));
    }

}
