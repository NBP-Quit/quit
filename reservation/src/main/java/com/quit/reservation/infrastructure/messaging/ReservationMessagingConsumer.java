package com.quit.reservation.infrastructure.messaging;

import com.quit.reservation.application.dto.CreateReservationDto;
import com.quit.reservation.application.service.ReservationService;
import com.quit.reservation.domain.enums.ReservationStatus;
import com.quit.reservation.infrastructure.messaging.message.PaymentMessage;
import com.quit.reservation.infrastructure.messaging.message.ReservationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Slf4j
@Component
public class ReservationMessagingConsumer {

    /* 수신할 메시지(topic)
     * 1. 결제에서 보내는 결제 성공/실패 메시지(성공 상태/실패 상태 변경)
     * 2. 가게에서 예약 확정 실패 메시지(실패 시 상태 변경) */

    private final ReservationService reservationService;

    public ReservationMessagingConsumer(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    //TODO: topics 환경변수 설정 고려

    // 대기열 -> 예약 서비스 메시지 수신 처리
    @KafkaListener(topics = "queue.process.success", groupId = "reservation-group")
    public void listenReservationCreate(ReservationMessage message) {
        log.info("예약 정보 메시지 수신 - 가게 ID: {}", message.getStoreId());

        UUID storeId = UUID.fromString(message.getStoreId());
        Integer guestCount = Integer.valueOf(message.getGuestCount());
        LocalDate reservationDate = LocalDate.parse(message.getReservationDate());
        LocalTime reservationTime = LocalTime.parse(message.getReservationTime());

        CreateReservationDto request = CreateReservationDto
                .of(storeId, guestCount, reservationDate, reservationTime);
        log.info("예약 정보 메시지 처리 호출");
        reservationService.createReservation(request, message.getUserEmail());
    }

    // 결제 -> 예약 서비스 메시지 수신 처리(성공)
    @KafkaListener(topics = "payment.create.success", groupId = "reservation-group")
    public void listenReservationPaymentSuccess(PaymentMessage message) {
        log.info("예약 결제 메시지 수신 - 결제 ID: {}", message.getPaymentId());

        UUID reservationId = message.getReservationId();
        Integer amount = message.getAmount();

        log.info("예약 금액 정보 업데이트 호출");
        reservationService.updateReservationPayment(reservationId, amount);

        log.info("예약 상태 변경 호출");
        ReservationStatus status = ReservationStatus.ACCEPTED;
        reservationService.changeReservationStatusAsync(reservationId, status);
    }

    @KafkaListener(topics = "payment.create.failed", groupId = "reservation-group")
    public void listenReservationPaymentFailed(PaymentMessage message) {
        log.info("예약 결제 실패 메시지 수신 - 결제 ID: {}", message.getPaymentId());

        UUID reservationId = message.getReservationId();
        cancelReservationAsync(reservationId);
    }

    //TODO: 랜덤 값 수정 및 topics, message 정의 필요
    @KafkaListener(topics = "store.reservation.failed", groupId = "reservation-group")
    public void listenReservationConfirmFailed() {
        log.info("예약 확정 실패 메시지 수신 - 예약 ID: {}", "실패");

        UUID reservationId = UUID.randomUUID();
        cancelReservationAsync(reservationId);
    }

    private void cancelReservationAsync(UUID reservationId) {
        log.info("예약 취소 처리 호출");
        reservationService.cancelReservationAsync(reservationId);
    }
}
