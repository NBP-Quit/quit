package com.quit.reservation;

import com.quit.reservation.application.service.ReservationService;
import com.quit.reservation.domain.enums.Role;
import com.quit.reservation.domain.model.Reservation;
import com.quit.reservation.domain.repository.ReservationRepository;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ReservationDeleteTest {

    private static final Logger logger = LoggerFactory.getLogger(ReservationDeleteTest.class);

    @Autowired
    ReservationService reservationService;

    @Autowired
    ReservationRepository reservationRepository;

    // Test용 Reservation 임시 데이터 필요
    @Test
    @DisplayName("예약 삭제 성공 테스트")
    void deleteReservation_successTest() {
        UUID reservationId = UUID.fromString("ef7451a4-6c96-4e25-ad6d-ab362d6cada1");
        Role role = Role.MASTER;
        String managerId = "manager";

        reservationService.deleteReservation(reservationId, managerId, role);

        Reservation reservation = reservationRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new NotFoundException("예약을 찾을 수 없습니다."));

        assertEquals(true, reservation.getIsDeleted());
    }

    @Test
    @DisplayName("권한 없는 사용자로 예약 삭제 실패 테스트")
    void deleteReservation_noPermissionTest() {
        UUID reservationId = UUID.fromString("ef7451a4-6c96-4e25-ad6d-ab362d6cada1");
        Role role = Role.USER;
        String managerId = "manager";

        assertThrows(IllegalArgumentException.class, () ->
                reservationService.deleteReservation(reservationId, managerId, role)
        );
    }

    @Test
    @DisplayName("존재하지 않는 예약 삭제 실패 테스트")
    void deleteReservation_notFoundTest() {
        UUID reservationId = UUID.randomUUID();
        Role role = Role.MASTER;
        String managerId = "manager";

        assertThrows(RuntimeException.class, () ->
                reservationService.deleteReservation(reservationId, managerId, role)
        );
    }

    @Test
    @DisplayName("삭제 불가능한 상태의 예약 삭제 실패 테스트")
    void deleteReservation_invalidStatusTest() {
        UUID reservationId = UUID.fromString("a03e1d63-9956-4bc2-9f15-6e49f8c0ffa4");
        Role role = Role.MASTER;
        String managerId = "manager";

        assertThrows(IllegalArgumentException.class, () ->
                reservationService.deleteReservation(reservationId, managerId, role)
        );
    }
}
