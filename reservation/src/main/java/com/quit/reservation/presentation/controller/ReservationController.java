package com.quit.reservation.presentation.controller;

import com.quit.reservation.application.dto.ChangeReservationStatusResponse;
import com.quit.reservation.application.dto.CreateReservationResponse;
import com.quit.reservation.application.dto.UpdateReservationResponse;
import com.quit.reservation.application.service.ReservationService;
import com.quit.reservation.common.dto.ApiResponse;
import com.quit.reservation.domain.enums.Role;
import com.quit.reservation.presentation.request.ChangeReservationStatusRequest;
import com.quit.reservation.presentation.request.CreateReservationRequest;
import com.quit.reservation.presentation.request.UpdateReservationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateReservationResponse>> createReservation(@RequestBody CreateReservationRequest request) {

        //TODO: customerId 임시값 사용 -> header 값으로 변경 (log 추가) 1
        String customerId = "testUser";
        return ResponseEntity.ok(ApiResponse.success(reservationService.createReservation(request.toDto(), customerId)));
    }

    @PatchMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<ChangeReservationStatusResponse>> changeReservationStatus(@PathVariable UUID reservationId,
                                                                                                @RequestBody ChangeReservationStatusRequest request) {

        //TODO: customerId 임시 값 사용, Status 변경 시 role 타입도 추후 파라미터로 추가 1
        String customerId = "testUser";
        return ResponseEntity.ok(ApiResponse.success(reservationService.changeReservationStatus(reservationId, request, customerId)));
    }

    //TODO: 예약 취소 반환 타입 고민(상태 변경과 동일하게 할 건지, 삭제와 동일하게 메시지만 반환할지)
    @PatchMapping("/{reservationId}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelReservation(@PathVariable UUID reservationId) {

        //TODO: customerId 임시 값 사용, Status 변경 시 role 타입도 추후 파라미터로 추가 2
        String customerId = "testUser";
        reservationService.cancelReservation(reservationId, customerId);
        return ResponseEntity.ok(ApiResponse.success("예약을 취소 했습니다."));
    }

    @PatchMapping("/{reservationId}/details")
    public ResponseEntity<ApiResponse<UpdateReservationResponse>> updateReservation(@PathVariable UUID reservationId,
                                                                                    @RequestBody UpdateReservationRequest request) {
        //TODO: customerId 임시값 사용 -> header 값으로 변경 (log 추가) 2
        String customerId = "testUser";
        return ResponseEntity.ok(ApiResponse.success(reservationService.updateReservationDetails(reservationId, request.toDto(), customerId)));
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<String>> deleteReservation(@PathVariable UUID reservationId, @RequestBody Role role) {

        //TODO: managerId 임시 값 사용, Role 값 추후 헤더 값으로 변경 예정(임시로 RequestBody 값 사용)
        String managerId = "testManager";
        reservationService.deleteReservation(reservationId, managerId, role);
        return ResponseEntity.ok(ApiResponse.success("예약을 삭제 했습니다."));
    }
}
