package com.quit.reservation.presentation.controller;

import com.quit.reservation.application.dto.CreateReservationResponse;
import com.quit.reservation.application.service.ReservationService;
import com.quit.reservation.common.dto.ApiResponse;
import com.quit.reservation.presentation.request.CreateReservationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateReservationResponse>> createReservation(@RequestBody CreateReservationRequest request) {

        //TODO: customerId 임시값 사용 -> header 값으로 변경 (log 추가)
        String customerId = "testUser";
        return ResponseEntity.ok(ApiResponse.success(reservationService.createReservation(request.toDto(), customerId)));
    }
}
