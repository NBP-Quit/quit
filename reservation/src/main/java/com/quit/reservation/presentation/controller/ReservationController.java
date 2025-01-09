package com.quit.reservation.presentation.controller;

import com.querydsl.core.types.Predicate;
import com.quit.reservation.application.dto.ChangeReservationStatusResponse;
import com.quit.reservation.application.dto.CreateReservationResponse;
import com.quit.reservation.application.dto.GetReservationResponse;
import com.quit.reservation.application.dto.ReservationResponse;
import com.quit.reservation.application.service.ReservationQueryService;
import com.quit.reservation.application.service.ReservationService;
import com.quit.reservation.common.dto.ApiResponse;
import com.quit.reservation.common.util.RoleUtil;
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
    private final RoleUtil roleUtil;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateReservationResponse>> createReservation(@RequestBody CreateReservationRequest request,
                                                                                    @RequestHeader("X-User-Email") String customerId) {

        return ResponseEntity.ok(ApiResponse.success(reservationService.createReservation(request.toDto(), customerId)));
    }

    @PatchMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<ChangeReservationStatusResponse>> changeReservationStatus(@PathVariable UUID reservationId,
                                                                                                @RequestBody ChangeReservationStatusRequest request,
                                                                                                @RequestHeader("X-User-Email") String customerId,
                                                                                                @RequestHeader("X-User-Role") String role) {
        String userRole = RoleUtil.cleanRole(role);
        return ResponseEntity.ok(ApiResponse.success(reservationService.changeReservationStatus(reservationId, request, customerId, userRole)));
    }

    @PatchMapping("/{reservationId}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelReservation(@PathVariable UUID reservationId,
                                                                 @RequestHeader("X-User-Email") String customerId,
                                                                 @RequestHeader("X-User-Role") String role) {

        String userRole = RoleUtil.cleanRole(role);
        reservationService.cancelReservation(reservationId, customerId, userRole);
        return ResponseEntity.ok(ApiResponse.success("예약을 취소 했습니다."));
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<String>> deleteReservation(@PathVariable UUID reservationId,
                                                                 @RequestHeader("X-User-Email") String managerId,
                                                                 @RequestHeader("X-User-Role") String role) {

        String userRole = RoleUtil.cleanRole(role);
        reservationService.deleteReservation(reservationId, managerId, userRole);
        return ResponseEntity.ok(ApiResponse.success("예약을 삭제 했습니다."));
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<ReservationResponse>> getReservation(@PathVariable UUID reservationId,
                                                                           @RequestHeader("X-User-Email") String customerId) {

        return ResponseEntity.ok(ApiResponse.success(reservationQueryService.getReservation(reservationId, customerId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<GetReservationResponse>> findReservations(@PageableDefault Pageable pageable,
                                                                                @RequestHeader("X-User-Email") String customerId) {

        return ResponseEntity.ok(ApiResponse.success(reservationQueryService.findReservations(customerId, pageable)));
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<ApiResponse<GetReservationResponse>> findReservationsByStore(@PathVariable UUID storeId,
                                                                                       @PageableDefault Pageable pageable,
                                                                                       @RequestHeader("X-User-Role") String role) {
        String ownerRole = RoleUtil.cleanRole(role);
        return ResponseEntity.ok(ApiResponse.success(
                reservationQueryService.findReservationsByStore(storeId, ownerRole, pageable)));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<GetReservationResponse>> findAllReservations(
            @QuerydslPredicate(root = Reservation.class) Predicate predicate,
            @PageableDefault Pageable pageable,
            @RequestHeader("X-User-Role") String role
    ) {

        String masterRole = RoleUtil.cleanRole(role);
        return ResponseEntity.ok(ApiResponse.success(
                reservationQueryService.findAllReservations(masterRole, predicate, pageable)));
    }

    @GetMapping("/{reservationId}/find")
    public ResponseEntity<ApiResponse<UUID>> findReservation(@PathVariable UUID reservationId) {
        return ResponseEntity.ok(ApiResponse.success(reservationQueryService.getReservation(reservationId)));
    }
}
