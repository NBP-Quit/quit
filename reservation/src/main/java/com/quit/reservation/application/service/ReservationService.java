package com.quit.reservation.application.service;

import com.quit.reservation.application.dto.CreateReservationDto;
import com.quit.reservation.application.dto.CreateReservationResponse;
import com.quit.reservation.domain.enums.ReservationStatus;
import com.quit.reservation.domain.model.Reservation;
import com.quit.reservation.domain.repository.ReservationRepository;
import com.quit.reservation.presentation.request.CreateReservationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
