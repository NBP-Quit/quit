package com.quit.reservation.application.service;

import com.quit.reservation.application.dto.CreateReservationDto;
import com.quit.reservation.application.dto.CreateReservationResponse;
import com.quit.reservation.domain.enums.ReservationStatus;
import com.quit.reservation.domain.model.Reservation;
import com.quit.reservation.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

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

    @Transactional
    public CreateReservationResponse createReservation(CreateReservationDto request, String customerId) {
        log.info("예약 생성 작업 시작");
        //TODO: Store application에서 예약 관련 정보 feign client로 받아오기

        //TODO: 데이터 검증 작업 : 추후 Store 정보 가져와서 비교해서 검증하는 것으로 변경
        validateReservationRequest(request);

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

    private void validateReservationRequest(CreateReservationDto request) {
        if (request.getStoreId() == null || request.getGuestCount() == null ||
                request.getReservationDate() == null || request.getReservationTime() == null) {
            throw new IllegalArgumentException("필수 입력 데이터가 누락되었습니다.");
        }

        if (request.getGuestCount() <= 0) {
            throw new IllegalArgumentException("예약 인원은 최소 1명 이상이어야 합니다.");
        }

        LocalDate today = LocalDate.now();
        LocalDate reservationDate = request.getReservationDate();
        if (reservationDate.isBefore(today) || reservationDate.isEqual(today)) {
            throw new IllegalArgumentException("예약할 수 없는 예약 날짜 입니다.");
        }

        LocalTime reservationTime = request.getReservationTime();
        if (reservationTime.isBefore(LocalTime.of(0, 0))
                || reservationTime.isAfter(LocalTime.of(23, 59))) {
            throw new IllegalArgumentException("예약할 수 없는 예약 시간 입니다.");
        }
    }
}
