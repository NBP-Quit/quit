package com.quit.reservation.application.service;

import com.quit.reservation.application.dto.*;
import com.quit.reservation.domain.enums.ReservationStatus;
import com.quit.reservation.domain.enums.Role;
import com.quit.reservation.domain.model.Reservation;
import com.quit.reservation.domain.repository.ReservationRepository;
import com.quit.reservation.presentation.request.ChangeReservationStatusRequest;
import jakarta.ws.rs.NotFoundException;
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

    /* 예약 생성 및 확정
     * 1. 가게에서 예약 정보 가져오기
     * 2. 예약 정보 임시 저장하기
     * 3. 가게로 예약 정보 보내고, 결제 시스템에 결제 요청 보내기
     * 4. 결제 완료되면 예약 상태 변경하기*/

    //TODO: Kafka event 추가 및 동시성 제어 구현 필요

    @Transactional
    public CreateReservationResponse createReservation(CreateReservationDto request, String customerId) {
        log.info("예약 생성 작업 시작");
        //TODO: Store application에서 예약 관련 정보 feign client로 받아오기

        //TODO: 데이터 검증 작업 : 추후 Store 정보 가져와서 비교해서 검증하는 것으로 변경
        validateCreateReservationRequest(request);

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

    @Transactional
    public ChangeReservationStatusResponse changeReservationStatus(UUID reservationId,
                                                                   ChangeReservationStatusRequest request,
                                                                   String customerId) {
        log.info("예약 상태 변경 작업 시작");
        log.info("상태 변경 예약 UUID : {}", reservationId);
        log.info("변경할 상태: {}", request.getReservationStatus());

        Reservation reservation = reservationRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new NotFoundException("예약을 찾을 수 없습니다."));

        //TODO: 취소 상태가 들어올 경우에 대한 예외 처리 변경하기
        if (request.getReservationStatus().equals(ReservationStatus.CANCELED)
                || reservation.getReservationStatus().equals(ReservationStatus.CANCELED)
                || reservation.getReservationStatus().equals(ReservationStatus.COMPLETED)) {
            throw new IllegalArgumentException("예약 진행 작업을 수행할 수 없는 예약 상태 입니다.");
        }

        //TODO: OWNER 이상의 권한을 가진 사람은 상태 변경을 할 수 있도록 수정
        if (reservation.getCustomerId().equals(customerId)) {
            reservation.changeStatus(request.getReservationStatus());
            log.info("예약 상태 변경 작업 완료");
            return ChangeReservationStatusResponse.fromReservation(reservation);
        }

        throw new IllegalArgumentException("예약 상태를 변경할 권한이 없습니다.");
    }

    @Transactional
    public void cancelReservation(UUID reservationId, String customerId) {
        //TODO: Owner 이상의 권한을 가지면 예약 취소 가능하도록 검증 추가
        log.info("예약 취소 작업 시작");
        Reservation reservation = reservationRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new NotFoundException("예약을 찾을 수 없습니다."));

        if (reservation.getCustomerId().equals(customerId)) {
            reservation.cancel();
            log.info("예약 취소 작업 완료");
            return;
        }

        throw new IllegalArgumentException("예약을 취소할 권한이 없습니다");
    }

    @Transactional
    public UpdateReservationResponse updateReservationDetails(UUID reservationId,
                                                              UpdateReservationDto request,
                                                              String customerId) {
        //TODO: 추후 Version 사용 데이터 검증 작업 추가
        log.info("예약 수정 작업 시작");
        Reservation reservation = reservationRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new NotFoundException("예약을 찾을 수 없습니다."));

        if (reservation.getCustomerId().equals(customerId)) {
            //TODO: 가게의 예약 정보를 확인해 변경 가능 여부 확인하는 메서드 추가
            validateReservationRequest(request.getGuestCount(),
                    request.getReservationDate(), request.getReservationTime());
            reservation.updateDetails(request.getGuestCount(),
                    request.getReservationDate(), request.getReservationTime());
            return UpdateReservationResponse.fromReservation(reservation);
        }

        throw new IllegalArgumentException("예약을 변경할 권한이 없습니다.");
    }

    @Transactional
    public void deleteReservation(UUID reservationId, String managerId, Role role) {
        log.info("예약 삭제 작업 시작");
        log.info("관리자: {}", managerId);

        if (!(role.equals(Role.MANAGER) || role.equals(Role.MASTER))) {
            throw new IllegalArgumentException("예약을 삭제할 권한이 없습니다.");
        }

        Reservation reservation = reservationRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new NotFoundException("예약을 찾을 수 없습니다."));

        if (reservation.getReservationStatus().equals(ReservationStatus.CANCELED)) {
            //TODO: BaseEntity 연결 후 삭제 시 관리자 아이디 추가(deletedBy)
            reservation.markDeleted();
            log.info("예약 삭제 작업 완료");
            return;
        }

        throw new IllegalArgumentException("예약을 삭제할 수 없는 예약 상태입니다.");
    }

    //TODO: 검증 메서드 리팩토링 작업 필요
    private void validateCreateReservationRequest(CreateReservationDto request) {
        if (request.getStoreId() == null || request.getGuestCount() == null ||
                request.getReservationDate() == null || request.getReservationTime() == null) {
            throw new IllegalArgumentException("필수 입력 데이터가 누락되었습니다.");
        }

        validateReservationRequest(request.getGuestCount(),
                request.getReservationDate(), request.getReservationTime());
    }

    private void validateReservationRequest(Integer guestCount,
                                            LocalDate reservationDate,
                                            LocalTime reservationTime) {
        if (guestCount <= 0) {
            throw new IllegalArgumentException("예약 인원은 최소 1명 이상이어야 합니다.");
        }

        LocalDate today = LocalDate.now();

        if (reservationDate.isBefore(today) || reservationDate.isEqual(today)) {
            throw new IllegalArgumentException("예약할 수 없는 예약 날짜 입니다.");
        }

        if (reservationTime.isBefore(LocalTime.of(0, 0))
                || reservationTime.isAfter(LocalTime.of(23, 59))) {
            throw new IllegalArgumentException("예약할 수 없는 예약 시간 입니다.");
        }
    }
}
