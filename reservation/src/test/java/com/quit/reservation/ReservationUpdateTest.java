package com.quit.reservation;

import com.quit.reservation.application.dto.UpdateReservationDto;
import com.quit.reservation.application.dto.UpdateReservationResponse;
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
public class ReservationUpdateTest {

    private static final Logger logger = LoggerFactory.getLogger(ReservationUpdateTest.class);

    @Autowired
    ReservationService reservationService;

    @Autowired
    ReservationRepository reservationRepository;

    // Test용 Reservation 임시 데이터 필요
    @Test
    @DisplayName("예약 변경 성공 테스트")
    void updateReservation_successTest() {
        UUID reservationId = UUID.fromString("a03e1d63-9956-4bc2-9f15-6e49f8c0ffa4");
        UpdateReservationDto request = UpdateReservationDto.of(
                5,
                LocalDate.of(2025, 1, 15),
                LocalTime.of(15, 0)
        );
        String customerId = "testUser";

        UpdateReservationResponse response = reservationService.updateReservationDetails(
                reservationId, request, customerId);

        logger.info("예약 인원 수: {}", response.getGuestCount());
        logger.info("예약 일: {}", response.getReservationDate());
        logger.info("예약 시간: {}", response.getReservationTime());
    }

    @Test
    @DisplayName("예약 변경 실패 테스트 - 존재하지 않는 예약")
    void updateReservation_notFoundTest() {
        UUID reservationId = UUID.randomUUID();
        UpdateReservationDto request = UpdateReservationDto.of(
                5,
                LocalDate.of(2025, 1, 15),
                LocalTime.of(15, 0)
        );
        String customerId = "testUser";

        assertThrows(RuntimeException.class, () ->
                reservationService.updateReservationDetails(reservationId, request, customerId)
        );
    }

    @Test
    @DisplayName("예약 변경 실패 테스트 - 변경 불가능한 예약 상태")
    void updateReservation_invalidStatusTest() {
        UUID reservationId = UUID.fromString("ef7451a4-6c96-4e25-ad6d-ab362d6cada1");
        UpdateReservationDto request = UpdateReservationDto.of(
                5,
                LocalDate.of(2025, 1, 15),
                LocalTime.of(15, 0)
        );
        String customerId = "testUser";

        assertThrows(IllegalArgumentException.class, () ->
                reservationService.updateReservationDetails(reservationId, request, customerId)
        );
    }

    @Test
    @DisplayName("예약 변경 실패 테스트 - 권한 없는 유저")
    void updateReservation_noPermissionTest() {
        UUID reservationId = UUID.fromString("a03e1d63-9956-4bc2-9f15-6e49f8c0ffa4");
        UpdateReservationDto request = UpdateReservationDto.of(
                5,
                LocalDate.of(2025, 1, 15),
                LocalTime.of(15, 0)
        );
        String customerId = "customer";

        assertThrows(IllegalArgumentException.class, () ->
                reservationService.updateReservationDetails(reservationId, request, customerId)
        );
    }

    @Test
    @DisplayName("예약 변경 실패 테스트 - 예약 인원 최소 값 위반")
    void updateReservation_invalidGuestCountTest() {
        UUID reservationId = UUID.fromString("a03e1d63-9956-4bc2-9f15-6e49f8c0ffa4");
        UpdateReservationDto request = UpdateReservationDto.of(
                0,
                LocalDate.of(2025, 1, 15),
                LocalTime.of(15, 0)
        );
        String customerId = "testUser";

        assertThrows(IllegalArgumentException.class, () ->
                reservationService.updateReservationDetails(reservationId, request, customerId)
        );
    }

    @Test
    @DisplayName("예약 변경 실패 테스트 - 예약 날짜 위반")
    void updateReservation_invalidReservationDateTest() {
        UUID reservationId = UUID.fromString("a03e1d63-9956-4bc2-9f15-6e49f8c0ffa4");
        UpdateReservationDto request = UpdateReservationDto.of(
                4,
                LocalDate.of(2024, 1, 15),
                LocalTime.of(15, 0)
        );
        String customerId = "testUser";

        assertThrows(IllegalArgumentException.class, () ->
                reservationService.updateReservationDetails(reservationId, request, customerId)
        );
    }

    @Test
    @DisplayName("예약 변경 실패 테스트 - 예약 시간 위반")
    void updateReservation_invalidReservationTimeTest() {
        UUID reservationId = UUID.fromString("a03e1d63-9956-4bc2-9f15-6e49f8c0ffa4");
        UpdateReservationDto request = UpdateReservationDto.of(
                4,
                LocalDate.of(2025, 1, 15),
                null
        );
        String customerId = "testUser";

        assertThrows(NullPointerException.class, () ->
                reservationService.updateReservationDetails(reservationId, request, customerId)
        );
    }
}
