package com.quit.reservation;

import com.quit.reservation.application.dto.ChangeReservationStatusResponse;
import com.quit.reservation.application.service.ReservationService;
import com.quit.reservation.domain.enums.ReservationStatus;
import com.quit.reservation.domain.model.Reservation;
import com.quit.reservation.domain.repository.ReservationRepository;
import com.quit.reservation.presentation.request.ChangeReservationStatusRequest;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ReservationChangeStatusTest {

    private static final Logger logger = LoggerFactory.getLogger(ReservationChangeStatusTest.class);

    @Autowired
    ReservationService reservationService;

    @Autowired
    ReservationRepository reservationRepository;

    // Test용 Reservation 임시 데이터 필요

    @Test
    @DisplayName("예약 상태 변경 성공 테스트")
    void changeReservationStatus_successTest() {
        UUID reservationId = UUID.fromString("ef7451a4-6c96-4e25-ad6d-ab362d6cada1");
        ChangeReservationStatusRequest request = new ChangeReservationStatusRequest(ReservationStatus.CONFIRMED);
        String customerId = "testUser";

        ChangeReservationStatusResponse response = reservationService.changeReservationStatus(reservationId, request, customerId);

        logger.info("상태 변경된 예약 UUID: {}", response.getReservationId());
        assertEquals(ReservationStatus.CONFIRMED, response.getReservationStatus());
    }

    @Test
    @DisplayName("예약 상태 변경 실패 - 예약을 찾을 수 없는 경우")
    void changeReservationStatus_notFoundTest() {
        UUID reservationId = UUID.randomUUID(); // 존재하지 않는 ID
        ChangeReservationStatusRequest request = new ChangeReservationStatusRequest(ReservationStatus.CONFIRMED);
        String customerId = "testUser";

        assertThrows(RuntimeException.class, () ->
                reservationService.changeReservationStatus(reservationId, request, customerId)
        );
    }

    @Test
    @DisplayName("예약 상태 변경 실패 - 변경할 수 없는 상태")
    void changeReservationStatus_invalidStateTest() {
        UUID reservationId = UUID.fromString("ef7451a4-6c96-4e25-ad6d-ab362d6cada1");
        ChangeReservationStatusRequest request = new ChangeReservationStatusRequest(ReservationStatus.CANCELED);
        String customerId = "testUser";

        assertThrows(IllegalArgumentException.class, () ->
                reservationService.changeReservationStatus(reservationId, request, customerId)
        );
    }

    @Test
    @DisplayName("예약 상태 변경 실패 - 권한 없음")
    void changeReservationStatus_noPermissionTest() {
        UUID reservationId = UUID.fromString("ef7451a4-6c96-4e25-ad6d-ab362d6cada1");
        ChangeReservationStatusRequest request = new ChangeReservationStatusRequest(ReservationStatus.CONFIRMED);
        String unauthorizedCustomerId = "unauthorized_customer";

        assertThrows(IllegalArgumentException.class, () ->
                reservationService.changeReservationStatus(reservationId, request, unauthorizedCustomerId)
        );
    }

    @Test
    @DisplayName("예약 취소 성공 테스트")
    void cancelReservation_successTest() {
        UUID reservationId = UUID.fromString("ef7451a4-6c96-4e25-ad6d-ab362d6cada1");
        String customerId = "testUser";

        assertDoesNotThrow(() ->
                reservationService.cancelReservation(reservationId, customerId)
        );

        Reservation reservation = reservationRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new NotFoundException("예약을 찾을 수 없습니다."));

        assertEquals(ReservationStatus.CANCELED, reservation.getReservationStatus());
    }

    @Test
    @DisplayName("예약 취소 실패 - 권한 없음")
    void cancelReservation_noPermissionTest() {
        UUID reservationId = UUID.fromString("ef7451a4-6c96-4e25-ad6d-ab362d6cada1");
        String unauthorizedCustomerId = "unauthorized_customer";

        assertThrows(IllegalArgumentException.class, () ->
                reservationService.cancelReservation(reservationId, unauthorizedCustomerId)
        );
    }

    @Test
    @DisplayName("예약 취소 실패 - 예약을 찾을 수 없는 경우")
    void cancelReservation_notFoundTest() {
        UUID reservationId = UUID.randomUUID(); // 존재하지 않는 ID
        String customerId = "testUser";

        assertThrows(RuntimeException.class, () ->
                reservationService.cancelReservation(reservationId, customerId)
        );
    }
}
