package com.quit.reservation.application.service;

import com.quit.reservation.application.dto.ChangeReservationStatusResponse;
import com.quit.reservation.application.dto.CreateReservationDto;
import com.quit.reservation.application.dto.CreateReservationResponse;
import com.quit.reservation.domain.enums.ReservationStatus;
import com.quit.reservation.domain.enums.Role;
import com.quit.reservation.domain.model.Reservation;
import com.quit.reservation.domain.repository.ReservationRepository;
import com.quit.reservation.infrastructure.client.ReservationSlotResponse;
import com.quit.reservation.presentation.exception.CustomException;
import com.quit.reservation.presentation.exception.error.ErrorType;
import com.quit.reservation.presentation.request.ChangeReservationStatusRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationSlotClientService reservationSlotClientService;
    /* 예약 생성 및 확정
     * 1. 가게에서 예약 정보 가져오기
     * 2. 예약 정보 임시 저장하기
     * 3. 가게로 예약 정보 보내고, 결제 시스템에 결제 요청 보내기
     * 4. 결제 완료되면 예약 상태 변경하기*/

    //TODO: Kafka event 추가 및 동시성 제어 구현 필요
    //TODO: 검증 메서드 클래스로 분리 or 서비스 클래스 분리 고려(Kafka 사용/미사용)
    //TODO: 코드 리팩토링!!!

    @Transactional
    public CreateReservationResponse createReservation(CreateReservationDto request, String customerId) {
        log.info("예약 생성 작업 시작");
        ReservationSlotResponse response = reservationSlotClientService
                .getSlotByDateAndTime(request.getStoreId(),
                        request.getReservationDate(),
                        request.getReservationTime())
                        .getData();

        validateCreateReservationRequest(request);
        Boolean isAvailable = response.getIsAvailable();
        int availableCapacity = response.getMaxCapacity() - response.getCurrentCapacity();

        if (isAvailable.equals(true) && availableCapacity >= request.getGuestCount()) {
            Reservation reservation = reservationRepository.save(
                    Reservation.create(customerId, request.getStoreId(),
                            request.getGuestCount(), request.getReservationDate(), request.getReservationTime(),
                            ReservationStatus.PENDING, 0)
            );

            //TODO: Kafka를 사용해 store와 payment로 메시지 전송
            log.info("예약 UUID : {}", reservation.getReservationId());
            log.info("예약 정보 생성 완료");
            return CreateReservationResponse.of(reservation.getReservationId());
        }

        log.info("예약 정보 생성 실패 - 예약 불가능");
        throw new CustomException(ErrorType.FAILED_CREATED_RESERVATION);
    }

    @Transactional
    public ChangeReservationStatusResponse changeReservationStatus(UUID reservationId,
                                                                   ChangeReservationStatusRequest request,
                                                                   String customerId) {
        log.info("예약 상태 변경 작업 시작");
        log.info("상태 변경 예약 UUID : {}", reservationId);
        log.info("변경할 상태: {}", request.getReservationStatus());

        Reservation reservation = reservationRepository.findByReservationIdIsDeletedFalse(reservationId)
                .orElseThrow(() -> new CustomException(ErrorType.NOT_FOUND_RESERVATION));

        //TODO: 취소 상태가 들어올 경우에 대한 예외 처리 변경하기
        if (request.getReservationStatus().equals(ReservationStatus.CANCELED)
                || reservation.getReservationStatus().equals(ReservationStatus.CANCELED)
                || reservation.getReservationStatus().equals(ReservationStatus.COMPLETED)) {
            throw new CustomException(ErrorType.FAILED_CHANGE_RESERVATION_STATUS);
        }

        //TODO: OWNER 이상의 권한을 가진 사람은 상태 변경을 할 수 있도록 수정
        if (reservation.getCustomerId().equals(customerId)) {
            reservation.changeStatus(request.getReservationStatus());
            log.info("예약 상태 변경 작업 완료");
            return ChangeReservationStatusResponse.fromReservation(reservation);
        }

        throw new CustomException(ErrorType.ACCESS_DENIED);
    }

    @Transactional
    public void cancelReservation(UUID reservationId, String customerId) {
        //TODO: Owner 이상의 권한을 가지면 예약 취소 가능하도록 검증 추가
        log.info("예약 취소 작업 시작");
        Reservation reservation = reservationRepository.findByReservationIdIsDeletedFalse(reservationId)
                .orElseThrow(() -> new CustomException(ErrorType.NOT_FOUND_RESERVATION));

        if (reservation.getCustomerId().equals(customerId)) {
            reservation.cancel();
            log.info("예약 취소 작업 완료");
            return;
        }

        throw new CustomException(ErrorType.ACCESS_DENIED);
    }

    @Transactional
    public void deleteReservation(UUID reservationId, String managerId, Role role) {
        log.info("예약 삭제 작업 시작");
        log.info("관리자: {}", managerId);

        if (!(role.equals(Role.MANAGER) || role.equals(Role.MASTER))) {
            throw new CustomException(ErrorType.ACCESS_DENIED);
        }

        Reservation reservation = reservationRepository.findByReservationIdIsDeletedFalse(reservationId)
                .orElseThrow(() -> new CustomException(ErrorType.NOT_FOUND_RESERVATION));

        if (reservation.getReservationStatus().equals(ReservationStatus.CANCELED)) {
            //TODO: BaseEntity 연결 후 삭제 시 관리자 아이디 추가(deletedBy)
            reservation.markDeleted();
            log.info("예약 삭제 작업 완료");
            return;
        }

        throw new CustomException(ErrorType.FAILED_CHANGE_RESERVATION_STATUS);
    }

    //TODO: 검증 메서드 리팩토링 작업 필요
    private void validateCreateReservationRequest(CreateReservationDto request) {
        if (request.getStoreId() == null || request.getGuestCount() == null ||
                request.getReservationDate() == null || request.getReservationTime() == null) {
            throw new CustomException(ErrorType.COMMON_INVALID_PARAMETER, "필수 입력값이 누락되었습니다.");
        }

        int guestCount = request.getGuestCount();
        if (guestCount <= 0) {
            throw new CustomException(ErrorType.COMMON_INVALID_PARAMETER, "예약 인원은 최소 1명 이상이어야 합니다.");
        }

        LocalDate today = LocalDate.now();
        LocalDate reservationDate = request.getReservationDate();
        if (reservationDate.isBefore(today) || reservationDate.isEqual(today)) {
            throw new CustomException(ErrorType.COMMON_INVALID_PARAMETER, "잘못된 날짜 값입니다.");
        }

        LocalTime reservationTime = request.getReservationTime();
        if (reservationTime.isBefore(LocalTime.of(0, 0))
                || reservationTime.isAfter(LocalTime.of(23, 59))) {
            throw new CustomException(ErrorType.COMMON_INVALID_PARAMETER, "잘못된 시간 값입니다.");
        }
    }
}
