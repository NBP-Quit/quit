package com.quit.reservation.application.service;

import com.quit.reservation.application.dto.ChangeReservationStatusResponse;
import com.quit.reservation.application.dto.CreateReservationDto;
import com.quit.reservation.application.dto.CreateReservationResponse;
import com.quit.reservation.domain.enums.ReservationStatus;
import com.quit.reservation.domain.enums.Role;
import com.quit.reservation.domain.model.Reservation;
import com.quit.reservation.domain.repository.ReservationRepository;
import com.quit.reservation.domain.service.ReservationValidationService;
import com.quit.reservation.infrastructure.client.ReservationSlotResponse;
import com.quit.reservation.infrastructure.lock.DistributedLock;
import com.quit.reservation.infrastructure.messaging.MessageProducer;
import com.quit.reservation.presentation.exception.CustomException;
import com.quit.reservation.presentation.exception.error.ErrorType;
import com.quit.reservation.presentation.request.ChangeReservationStatusRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationValidationService validationService;
    private final MessageProducer messageProducer;
    private final ReservationSlotClientService reservationSlotClientService;
    private final ReservationValidationService reservationValidationService;
    /* 예약 생성 및 확정
     * 1. 가게에서 예약 정보 가져오기
     * 2. 예약 정보 임시 저장하기
     * 3. 가게로 예약 정보 보내고, 결제 시스템에 결제 요청 보내기
     * 4. 결제 완료되면 예약 상태 변경하기*/

    //TODO: 서비스 동시성 제어
    //TODO: OWNER 권한에 대한 본인 가게 여부 확인
    //TODO: 동기 처리 시 취소 제외 다른 상태로 변경 하는 건 OWNER 이상의 권한만 되도록 고려
    //TODO: 코드 리팩토링!!!

    @DistributedLock(key = "#request.storeId")
    public CreateReservationResponse createReservation(CreateReservationDto request, String customerId) {
        log.info("예약 생성 작업 시작");
        ReservationSlotResponse response = reservationSlotClientService
                .getSlotByDateAndTime(request.getStoreId(), request.getReservationDate(), request.getReservationTime())
                .getData();

        log.info("예약 슬롯 ID: {}", response.getSlotId());

        validateCreateReservationRequest(request);
        Boolean isAvailable = response.getIsAvailable();
        int availableCapacity = response.getMaxCapacity() - response.getCurrentCapacity();

        if (isAvailable.equals(true) && availableCapacity >= request.getGuestCount()) {
            Reservation reservation = reservationRepository.save(
                    Reservation.create(customerId, request.getStoreId(),
                            request.getGuestCount(), request.getReservationDate(),
                            request.getReservationTime(), ReservationStatus.PENDING,
                            0, response.getSlotId())
            );

            log.info("예약 UUID : {}", reservation.getReservationId());
            log.info("예약 정보 생성 완료");
            return CreateReservationResponse.of(reservation.getReservationId());
        }

        log.info("예약 정보 생성 실패");
        throw new CustomException(ErrorType.FAILED_CREATED_RESERVATION);
    }

    @Transactional
    public ChangeReservationStatusResponse changeReservationStatus(UUID reservationId,
                                                                   ChangeReservationStatusRequest request,
                                                                   String customerId,
                                                                   String userRole) {
        log.info("예약 상태 변경 작업 시작");
        log.info("상태 변경 예약 UUID : {}", reservationId);
        log.info("변경할 상태: {}", request.getReservationStatus());
        Reservation reservation = findReservation(reservationId);

        validationService.validateChangeReservationStatus(
                request.getReservationStatus(), reservation.getReservationStatus());
        assertPermission(reservation.getCustomerId(), customerId, userRole);

        reservation.changeStatus(request.getReservationStatus());
        log.info("예약 상태 변경 작업 완료");
        return ChangeReservationStatusResponse.fromReservation(reservation);
    }

    @DistributedLock(key = "#reservationId")
    public void changeReservationStatusAsync(UUID reservationId, ReservationStatus status) {
        log.info("비동기 예약 상태 변경 시작");
        Reservation reservation = findReservation(reservationId);
        validationService.validateChangeReservationStatus(status, reservation.getReservationStatus());

        reservation.changeStatus(status);
        log.info("예약 정보 메시지 전송");
        messageProducer.sendReservationData(reservation.getSlotId(), reservation.getGuestCount());
        log.info("비동기 예약 상태 변경 완료");
    }

    @Transactional
    public void cancelReservation(UUID reservationId, String customerId, String userRole) {
        log.info("예약 취소 작업 시작");
        Reservation reservation = findReservation(reservationId);

        validationService.validateCancelReservationStatus(reservation.getReservationStatus());
        assertPermission(reservation.getCustomerId(), customerId, userRole);

        reservation.cancel();
        sendCancelReservationMessage(reservation);
        log.info("예약 취소 작업 완료");
    }

    @Transactional
    public void cancelReservationAsync(UUID reservationId) {
        log.info("비동기 예약 취소 작업 시작");
        Reservation reservation = findReservation(reservationId);
        validationService.validateCancelReservationStatus(reservation.getReservationStatus());
        reservation.cancel();
        sendCancelReservationMessage(reservation);
        log.info("비동기 예약 취소 작업 완료");
    }

    @Transactional
    public void deleteReservation(UUID reservationId, String managerId, String userRole) {
        log.info("예약 삭제 작업 시작");
        log.info("관리자: {}", managerId);

        if (userRole.equals(Role.MASTER.name())) {
            Reservation reservation = findReservation(reservationId);
            log.info("예약 상태 확인: {}", reservation.getReservationStatus());
            reservationValidationService.validateReservationStatusForDelete(reservation.getReservationStatus());
            reservation.markAsDeleted(managerId);
            log.info("예약 삭제 작업 완료");
        }

        throw new CustomException(ErrorType.ACCESS_DENIED);
    }

    @Transactional
    public void updateReservationPayment(UUID reservationId, Integer amount) {
        log.info("예약 호출");
        Reservation reservation = findReservation(reservationId);

        log.info("예약 금액 업데이트 시작");
        reservation.updateReservationPrice(amount);
        log.info("예약 금액 업데이트 완료");
    }

    private Reservation findReservation(UUID reservationId) {
        return reservationRepository.findByReservationIdIsDeletedFalse(reservationId)
                .orElseThrow(() -> new CustomException(ErrorType.NOT_FOUND_RESERVATION));
    }

    //TODO: 검증 메서드 리팩토링 작업 필요
    private void validateCreateReservationRequest(CreateReservationDto request) {
        if (request.getStoreId() == null || request.getGuestCount() == null ||
                request.getReservationDate() == null || request.getReservationTime() == null) {
            throw new CustomException(ErrorType.COMMON_INVALID_PARAMETER, "필수 입력값이 누락되었습니다.");
        }

        validationService.validateGuestCount(request.getGuestCount());
        validationService.validateReservationDate(request.getReservationDate());
        validationService.validateReservationTime(request.getReservationTime());
    }

    private void sendCancelReservationMessage(Reservation reservation) {
        messageProducer.sendReservationFailed(reservation.getSlotId(), reservation.getGuestCount());
        log.info("예약 삭제 정보 메시지 송신 완료");
    }

    private void assertPermission(String requestCustomerId, String customerId, String userRole) {
        if (requestCustomerId.equals(customerId)
                || userRole.equals(Role.OWNER.name())
                || userRole.equals(Role.MASTER.name())) {
            return;
        }
        throw new CustomException(ErrorType.ACCESS_DENIED);
    }
}
