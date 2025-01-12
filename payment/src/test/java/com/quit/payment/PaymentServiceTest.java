package com.quit.payment;

import com.quit.payment.application.dto.PaymentDto;
import com.quit.payment.application.dto.PaymentEvent;
import com.quit.payment.application.dto.TempPaymentDto;
import com.quit.payment.application.dto.res.PaymentResponse;
import com.quit.payment.application.dto.res.TempPaymentResponse;
import com.quit.payment.application.service.PaymentService;
import com.quit.payment.common.dto.ApiResponse;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.quit.payment.presentation.exception.ErrorType.PAYMENT_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private TempPaymentRepository tempPaymentRepository;

    @Mock
    private ReservationGateway reservationGateway;

    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private PaymentService paymentService;

    private UUID reservationId;
    private UUID paymentId;

    @BeforeEach
    void setUp() {
        reservationId = UUID.randomUUID();
        paymentId = UUID.randomUUID();
    }

    @Test
    @DisplayName("임시 결제를 생성할 수 있다.")
    void createTempPayment() {
        // given
        TempPaymentDto request = TempPaymentDto.of("ORDER123",1000);
        TempPayment tempPayment = TempPayment.of(request.getAmount(), request.getOrderId());
        when(tempPaymentRepository.save(any(TempPayment.class))).thenReturn(tempPayment);

        // when
        TempPaymentResponse response = paymentService.createTempPayment(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo(request.getOrderId());
        assertThat(response.getAmount()).isEqualTo(request.getAmount());
    }

    @Test
    @DisplayName("결제를 성공적으로 생성할 수 있다.")
    void createPayment() {
        // given
        PaymentDto request = PaymentDto.of("ORDER123", 1000, "PAYMENT_KEY_123");
        TempPayment tempPayment = TempPayment.of(request.getAmount(), request.getOrderId());
        when(tempPaymentRepository.findByOrderIdAndIsDeletedFalse(request.getOrderId()))
                .thenReturn(Optional.of(tempPayment));
        when(reservationGateway.getReservation(reservationId)).thenReturn(ApiResponse.success(reservationId));

        ConfirmPaymentResponse confirmResponse = new ConfirmPaymentResponse("PAYMENT_KEY_123", "ORDER123", "DONE" , 1000);
        when(paymentGateway.confirmPayment(request)).thenReturn(confirmResponse);

        Payment payment = Payment.of(1000, Status.SUCCESS, confirmResponse.getPaymentKey(), confirmResponse.getOrderId(), reservationId);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // when
        PaymentResponse response = paymentService.createPayment(reservationId, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getAmount()).isEqualTo(1000);
        assertThat(response.getOrderId()).isEqualTo("ORDER123");
        assertThat(response.getStatus()).isEqualTo(Status.SUCCESS);
        assertThat(response.getPaymentKey()).isEqualTo("PAYMENT_KEY_123");
        verify(paymentGateway, times(1)).confirmPayment(request);
        verify(kafkaProducer, times(1)).sendMessage(anyString(), anyString(), any(PaymentEvent.class));
    }

    @Test
    @DisplayName("결제를 취소할 수 있다.")
    void cancelPayment() {
        // given
        Payment payment = Payment.of(1000, Status.SUCCESS, "PAYMENT_KEY_123", "ORDER123", reservationId);
        when(paymentRepository.findByIdAndIsDeletedFalse(paymentId)).thenReturn(Optional.of(payment));

        CancelPaymentRequest cancelRequest = new CancelPaymentRequest("단순 변심");
        CancelPaymentResponse cancelResponse = new CancelPaymentResponse("PAYMENT_KEY_123", "ORDER123", "DONE" );
        when(paymentGateway.cancelPayment(payment.getPaymentKey(), cancelRequest)).thenReturn(cancelResponse);

        // when
        PaymentResponse response = paymentService.cancelPayment(paymentId, reservationId, cancelRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Status.CANCELED);
        verify(paymentGateway, times(1)).cancelPayment(payment.getPaymentKey(), cancelRequest);
        verify(kafkaProducer, times(1)).sendMessage(anyString(), anyString(), any(PaymentEvent.class));
    }

    @Test
    @DisplayName("예약 ID로 결제를 조회할 수 있다.")
    void getPaymentByReservation() {
        // given
        Payment payment = Payment.of(1000, Status.SUCCESS, "PAYMENT_KEY_123", "ORDER123", reservationId);
        when(paymentRepository.findByReservationIdAndIsDeletedFalse(reservationId))
                .thenReturn(Optional.of(payment));

        // when
        PaymentResponse response = paymentService.getPaymentByReservation(reservationId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getAmount()).isEqualTo(1000);
        assertThat(response.getOrderId()).isEqualTo("ORDER123");
        assertThat(response.getStatus()).isEqualTo(Status.SUCCESS);
        assertThat(response.getPaymentKey()).isEqualTo("PAYMENT_KEY_123");
        assertThat(response.getReservationId()).isEqualTo(reservationId);
        verify(paymentRepository, times(1)).findByReservationIdAndIsDeletedFalse(reservationId);
    }

    @Test
    @DisplayName("존재하지 않는 예약 ID로 조회 시 예외를 던진다.")
    void getPaymentByInvalidReservation() {
        // given
        when(paymentRepository.findByReservationIdAndIsDeletedFalse(reservationId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.getPaymentByReservation(reservationId))
                .isInstanceOf(CustomException.class)
                .hasMessage(PAYMENT_NOT_FOUND.getMessage());
    }

}
