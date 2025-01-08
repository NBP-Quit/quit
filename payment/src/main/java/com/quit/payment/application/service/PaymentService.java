package com.quit.payment.application.service;

import com.quit.payment.application.dto.PaymentDto;
import com.quit.payment.application.dto.PaymentEvent;
import com.quit.payment.application.dto.TempPaymentDto;
import com.quit.payment.application.dto.res.PaymentResponse;
import com.quit.payment.application.dto.res.TempPaymentResponse;
import com.quit.payment.domain.entity.Payment;
import com.quit.payment.domain.entity.Status;
import com.quit.payment.domain.entity.TempPayment;
import com.quit.payment.domain.repository.PaymentRepository;
import com.quit.payment.domain.repository.TempPaymentRepository;
import com.quit.payment.infrastructure.client.PaymentGateway;
import com.quit.payment.infrastructure.client.ReservationGateway;
import com.quit.payment.infrastructure.dto.CancelPaymentResponse;
import com.quit.payment.infrastructure.dto.ConfirmPaymentResponse;
import com.quit.payment.infrastructure.dto.ReservationResponse;
import com.quit.payment.infrastructure.kafka.KafkaProducer;
import com.quit.payment.presentation.dto.CancelPaymentRequest;
import com.quit.payment.presentation.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.quit.payment.presentation.exception.ErrorType.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TempPaymentRepository tempPaymentRepository;
    private final ReservationGateway reservationGateway;
    private final PaymentGateway paymentGateway;
    private final KafkaProducer kafkaProducer;
    private static final String PAYMENT_CREATE_SUCCESS = "payment.create.success";
    private static final String PAYMENT_CREATE_FAILED = "payment.create.failed";

    public TempPaymentResponse createTempPayment(TempPaymentDto request) {
        TempPayment tempPayment = TempPayment.of(request.getAmount(), request.getOrderId());
        tempPaymentRepository.save(tempPayment);
        return TempPaymentResponse.from(tempPayment);
    }

    public PaymentResponse createPayment(UUID reservationId, PaymentDto request) {
        TempPayment tempPayment = ValidatePayment(request.getOrderId());
        validateAmount(tempPayment, request.getAmount());
        // todo: errorDecoder 로 예외처리, fallbackmethod 처리
        ReservationResponse reservationResponse = reservationGateway.getReservation(reservationId).getData();
        log.info("reservationId= {}", reservationResponse.getReservationId());
        ConfirmPaymentResponse response = paymentGateway.confirmPayment(request);
        log.info("Confirm payment response: {}", response);
        Payment payment = create(response, reservationResponse.getReservationId());
        paymentRepository.save(payment);
        String key = "paymentId:" + payment.getId();
        kafkaProducer.sendMessage(
                PAYMENT_CREATE_SUCCESS,
                key,
                PaymentEvent.of(reservationResponse.getReservationId(), payment.getId(), payment.getAmount()));
        return PaymentResponse.from(payment);
    }

    public PaymentResponse cancelPayment(UUID paymentsId, UUID reservationId, CancelPaymentRequest request) {
        Payment payment = checkPayment(paymentsId);
        checkReservation(payment, reservationId);
        CancelPaymentResponse response = paymentGateway.cancelPayment(payment.getPaymentKey(), request);
        log.info("Cancel payment response: {}", response);
        payment.cancel(Status.CANCELED, request.getCancelReason());
        String key = "paymentId:" + payment.getId();
        kafkaProducer.sendMessage(
                PAYMENT_CREATE_FAILED,
                key,
                PaymentEvent.of(reservationId, payment.getId(), payment.getAmount())
        );
        return PaymentResponse.from(payment);
    }

    private void checkReservation(Payment payment, UUID reservationId) {
        if(!payment.getReservationId().equals(reservationId)) {
            throw new CustomException(RESERVATION_ID_MISMATCH);
        }

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
