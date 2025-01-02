package com.quit.reservation;

import com.quit.reservation.application.dto.CreateReservationDto;
import com.quit.reservation.application.dto.CreateReservationResponse;
import com.quit.reservation.application.service.ReservationService;
import com.quit.reservation.domain.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ReservationCreateTest {

    //TODO: 테스트 시 DB 환경 변수 입력 필요

    private static final Logger logger = LoggerFactory.getLogger(ReservationCreateTest.class);

    @Autowired
    ReservationService reservationService;

    @Autowired
    ReservationRepository reservationRepository;

    @Test
    @DisplayName("예약 정보 생성 성공 테스트")
    void createReservation_successTest() {
        CreateReservationDto request = CreateReservationDto.of(
                UUID.fromString("2e520ad1-a3ff-4d8c-bc75-e10061ccb5f2"),
                2,
                LocalDate.of(2025, 1, 2),
                LocalTime.of(16, 30)
        );

        String customerId = "customer";

        CreateReservationResponse response = reservationService.createReservation(request, customerId);

        logger.info("예약 UUID {}", response.getReservationId());
    }

    @Test
    @DisplayName("필수 입력 데이터 누락 테스트")
    void createReservation_missingRequiredDataTest() {
        CreateReservationDto request = CreateReservationDto.of(
                null, // Store ID가 null
                2,
                LocalDate.of(2025, 1, 2),
                LocalTime.of(16, 30)
        );

        String customerId = "customer";

        assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(request, customerId)
        );
    }

    @Test
    @DisplayName("예약 인원 최소값 위반 테스트")
    void createReservation_invalidGuestCountTest() {
        CreateReservationDto request = CreateReservationDto.of(
                UUID.fromString("2e520ad1-a3ff-4d8c-bc75-e10061ccb5f2"),
                0, // 0명 예약
                LocalDate.of(2025, 1, 2),
                LocalTime.of(16, 30)
        );

        String customerId = "customer";

        assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(request, customerId)
        );
    }

    @Test
    @DisplayName("예약 날짜 과거 테스트")
    void createReservation_invalidDateTest() {
        CreateReservationDto request = CreateReservationDto.of(
                UUID.fromString("2e520ad1-a3ff-4d8c-bc75-e10061ccb5f2"),
                2,
                LocalDate.of(2025, 1, 1), // 과거 날짜
                LocalTime.of(16, 30)
        );

        String customerId = "customer";

        assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(request, customerId)
        );
    }

    @Test
    @DisplayName("예약 시간 범위 초과 테스트")
    void createReservation_invalidTimeTest() {
        CreateReservationDto request = CreateReservationDto.of(
                UUID.fromString("2e520ad1-a3ff-4d8c-bc75-e10061ccb5f2"),
                2,
                LocalDate.of(2025, 1, 2),
                null // 잘못된 시간
        );

        String customerId = "customer";

        assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(request, customerId)
        );
    }
}
