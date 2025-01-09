package com.quit.reservation.presentation.controller;

import com.querydsl.core.types.Predicate;
import com.quit.reservation.application.dto.*;
import com.quit.reservation.application.service.ReservationQueryService;
import com.quit.reservation.application.service.ReservationService;
import com.quit.reservation.common.dto.ApiResponse;
import com.quit.reservation.domain.enums.Role;
import com.quit.reservation.domain.model.Reservation;
import com.quit.reservation.presentation.request.ChangeReservationStatusRequest;
import com.quit.reservation.presentation.request.CreateReservationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationQueryService reservationQueryService;

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

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<String>> deleteReservation(@PathVariable UUID reservationId) {

        //TODO: managerId 임시 값 사용, Role 값 추후 헤더 값으로 변경 예정(임시로 RequestBody 값 사용)
        String managerId = "testManager";
        Role managerRole = Role.MASTER;
        reservationService.deleteReservation(reservationId, managerId, managerRole);
        return ResponseEntity.ok(ApiResponse.success("예약을 삭제 했습니다."));
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<ReservationResponse>> getReservation(@PathVariable UUID reservationId) {

        //TODO: customerId 임시값 사용 -> header 값으로 변경 (log 추가) 3
        String customerId = "testUser";
        return ResponseEntity.ok(ApiResponse.success(reservationQueryService.getReservation(reservationId, customerId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<GetReservationResponse>> findReservations(@PageableDefault Pageable pageable) {
        //TODO: customerId 임시값 사용 header 값으로 변경
        String customerId = "testUser";
        return ResponseEntity.ok(ApiResponse.success(
                reservationQueryService.findReservations(customerId, pageable)));
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<ApiResponse<GetReservationResponse>> findReservationsByStore(@PathVariable UUID storeId,
                                                                                       @PageableDefault Pageable pageable) {
        //TODO: role 임시값 사용 - 본인 가게만 조회할 수 있도록 처리 고려
        Role role = Role.OWNER;
        return ResponseEntity.ok(ApiResponse.success(
                reservationQueryService.findReservationsByStore(storeId, role, pageable)));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<GetReservationResponse>> findAllReservations(
            @QuerydslPredicate(root = Reservation.class) Predicate predicate,
            @PageableDefault Pageable pageable) {

        //TODO: Role 임시 값 사용
        Role role = Role.MASTER;
        return ResponseEntity.ok(ApiResponse.success(
                reservationQueryService.findAllReservations(role, predicate, pageable)));
    }

    @GetMapping("/{reservationId}/find")
    public ResponseEntity<ApiResponse<UUID>> findReservation(@PathVariable UUID reservationId) {
        return ResponseEntity.ok(ApiResponse.success(reservationQueryService.getReservation(reservationId)));
    }
}
