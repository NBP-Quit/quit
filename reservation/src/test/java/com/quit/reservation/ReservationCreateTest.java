package com.quit.reservation;

import com.quit.reservation.application.dto.CreateReservationDto;
import com.quit.reservation.application.dto.CreateReservationResponse;
import com.quit.reservation.application.service.ReservationService;
import com.quit.reservation.common.dto.ApiResponse;
import com.quit.reservation.domain.repository.ReservationRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@SpringBootTest
public class ReservationCreateTest {

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
}
