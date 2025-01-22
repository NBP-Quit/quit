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
    private final IdempotencyService idempotencyService;
    private static final String PAYMENT_SUCCESS_TOPIC = "payment.create.success";
    private static final String PAYMENT_FAILED_TOPIC = "payment.create.failed";

    public TempPaymentResponse createTempPayment(TempPaymentDto request) {
        TempPayment tempPayment = TempPayment.of(request.getAmount(), request.getOrderId());
        tempPaymentRepository.save(tempPayment);
        return TempPaymentResponse.from(tempPayment);
    }

    public PaymentResponse createPayment(UUID reservationId, PaymentDto request) {
        checkIdempotency(request.getIdempotencyKey());
        validateRequest(request);
        UUID retrievedReservationId = checkReservationId(reservationId);
        ConfirmPaymentResponse response = confirmPaymentRequest(request);
        log.info("Confirm payment response: {}", response);
        Payment payment = create(response, retrievedReservationId);
        paymentRepository.save(payment);
        saveIdempotencyKey(request.getIdempotencyKey(), request.getPaymentKey());
        publishPaymentEvent(PAYMENT_SUCCESS_TOPIC, retrievedReservationId, payment);
        return PaymentResponse.from(payment);
    }

    public PaymentResponse cancelPayment(UUID paymentId, UUID reservationId, CancelPaymentRequest request) {
        checkIdempotency(request.getIdempotencyKey());
        Payment payment = validatePayment(paymentId, reservationId);
        CancelPaymentResponse response = cancelPaymentRequest(payment.getPaymentKey(), request);
        log.info("Cancel payment response: {}", response);
        payment.cancel(Status.CANCELED, request.getCancelReason());
        saveIdempotencyKey(request.getIdempotencyKey(), payment.getPaymentKey());
        publishPaymentEvent(PAYMENT_FAILED_TOPIC, payment.getReservationId(), payment);
        return PaymentResponse.from(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByReservation(UUID reservationId) {
        Payment payment = paymentRepository.findByReservationIdAndIsDeletedFalse(reservationId)
                .orElseThrow(() -> new CustomException(PAYMENT_NOT_FOUND));
        return PaymentResponse.from(payment);
    }

    private void checkIdempotency(String idempotencyKey) {
        String existingPaymentKey = idempotencyService.getPaymentKeyByIdempotencyKey(idempotencyKey);
        if (existingPaymentKey != null) {
            throw new CustomException(REQUEST_ALREADY_PROCESSED);
        }
    }

    private void validateRequest(PaymentDto request) {
        validateTempPayment(request);
    }

    private void validateTempPayment(PaymentDto request) {
        TempPayment tempPayment = checkTempPayment(request);
        validateAmount(tempPayment, request.getAmount());
    }

    private TempPayment checkTempPayment(PaymentDto request) {
        return tempPaymentRepository.findByOrderIdAndIsDeletedFalse(request.getOrderId())
                .orElseThrow(() -> new CustomException(PAYMENT_DATA_INVALID));
    }

    private void validateAmount(TempPayment tempPayment, Integer amount) {
        if(!tempPayment.getAmount().equals(amount)) {
            throw new CustomException(PAYMENT_DATA_INVALID);
        }
    }

    private UUID checkReservationId(UUID reservationId) {
        return reservationGateway.getReservation(reservationId).getData();
    }

    private ConfirmPaymentResponse confirmPaymentRequest(PaymentDto request) {
        return paymentGateway.confirmPayment(request.getIdempotencyKey(), request);
    }

    private Payment create(ConfirmPaymentResponse response, UUID reservationId) {
        return Payment.of(
                response.getTotalAmount(),
                Status.SUCCESS,
                response.getPaymentKey(),
                response.getOrderId(),
                reservationId);
    }

    private void saveIdempotencyKey(String idempotencyKey, String paymentKey) {
        idempotencyService.saveIdempotencyKey(idempotencyKey, paymentKey);
    }

    private void publishPaymentEvent(String topic, UUID reservationId, Payment payment) {
        sendKafkaMessage(
                topic,
                "paymentId:" + payment.getId(),
                PaymentEvent.of(reservationId, payment.getId(), payment.getAmount())
        );
    }

    private void sendKafkaMessage(String topic, String key, PaymentEvent event) {
        try {
            kafkaProducer.sendMessage(topic, key, event);
        } catch (Exception e) {
            log.error("Failed to send Kafka message: topic= {}, key= {}", topic, key);
            throw new CustomException(KAFKA_MESSAGE_SEND_FAILED);
        }
    }

    private Payment validatePayment(UUID paymentId, UUID reservationId) {
        Payment payment = checkPayment(paymentId);
        validateReservation(payment, reservationId);
        return payment;
    }

    private Payment checkPayment(UUID paymentsId) {
        return paymentRepository.findByIdAndIsDeletedFalse(paymentsId)
                .orElseThrow(() -> new CustomException(PAYMENT_NOT_FOUND));
    }

    private void validateReservation(Payment payment, UUID reservationId) {
        if(!payment.getReservationId().equals(reservationId)) {
            throw new CustomException(RESERVATION_ID_MISMATCH);
        }
    }

    private CancelPaymentResponse cancelPaymentRequest(String paymentId, CancelPaymentRequest request) {
        return paymentGateway.cancelPayment(request.getIdempotencyKey(), paymentId, request.toDto());
    }

}
