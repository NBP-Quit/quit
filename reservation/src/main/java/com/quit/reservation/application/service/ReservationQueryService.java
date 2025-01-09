package com.quit.reservation.application.service;

import com.querydsl.core.types.Predicate;
import com.quit.reservation.application.dto.GetReservationResponse;
import com.quit.reservation.application.dto.ReservationResponse;
import com.quit.reservation.domain.enums.Role;
import com.quit.reservation.domain.model.Reservation;
import com.quit.reservation.domain.repository.ReservationRepository;
import com.quit.reservation.presentation.exception.CustomException;
import com.quit.reservation.presentation.exception.error.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationQueryService {

    private final ReservationRepository reservationRepository;

    public ReservationResponse getReservation(UUID reservationId, String customerId) {
        log.info("개인 예약 단 건 조회 작업 시작");

        Reservation reservation = reservationRepository.findByReservationIdIsDeletedFalse(reservationId)
                .orElseThrow(() -> new CustomException(ErrorType.NOT_FOUND_RESERVATION));

        if (reservation.getCustomerId().equals(customerId)) {
            log.info("예약 ID: {}", reservation.getReservationId());
            log.info("개인 예약 단 건 조회 작업 완료");
            return ReservationResponse.fromReservation(reservation);
        }

        log.info("예약자 정보 불일치");
        throw new CustomException(ErrorType.CUSTOMER_ID_NOT_SAME);
    }

    public UUID getReservation(UUID reservationId) {
        log.info("예약 단 건 조회");
        Reservation reservation = reservationRepository.findByReservationIdIsDeletedFalse(reservationId)
                .orElseThrow(() -> new CustomException(ErrorType.NOT_FOUND_RESERVATION));
        return reservation.getReservationId();
    }

    public GetReservationResponse findReservations(String customerId, Pageable pageable) {
        log.info("예약 목록 조회 시작");
        Page<Reservation> reservationPage = reservationRepository
                .findReservationsByUser(customerId, pageable);
        log.info("예약 목록 조회 완료");
        return GetReservationResponse.of(reservationPage);
    }

    public GetReservationResponse findReservationsByStore(UUID storeId, String ownerRole, Pageable pageable) {
        log.info("가게 예약 목록 조회 시작");
        if (ownerRole.equals(Role.OWNER.name())) {
            Page<Reservation> reservationPage = reservationRepository.findReservationsByStore(storeId, pageable);
            log.info("가게 예약 목록 조회 완료");
            return GetReservationResponse.of(reservationPage);
        }

        throw new CustomException(ErrorType.ACCESS_DENIED);
    }

    public GetReservationResponse findAllReservations(String masterRole, Predicate predicate, Pageable pageable) {
        log.info("관리자 예약 목록 조회 시작");
        if (masterRole.equals(Role.MASTER.name())) {
            Page<Reservation> reservationPage = reservationRepository.findAllReservations(Role.MASTER, predicate, pageable);
            log.info("관리자 예약 목록 조회 완료");
            return GetReservationResponse.of(reservationPage);
        }

        throw new CustomException(ErrorType.ACCESS_DENIED);
    }
}
