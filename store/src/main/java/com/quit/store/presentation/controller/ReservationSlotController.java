package com.quit.store.presentation.controller;

import com.quit.store.application.dto.res.ReservationSlotResponse;
import com.quit.store.application.dto.res.GetReservationSlotResponse;
import com.quit.store.application.service.ReservationSlotService;
import com.quit.store.common.dto.ApiResponse;
import com.quit.store.common.util.PageableUtil;
import com.quit.store.presentation.dto.BatchReservationSlotsRequest;
import com.quit.store.presentation.dto.CreateReservationSlotRequest;
import com.quit.store.presentation.dto.UpdateReservationSlotRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores/{storeId}/reservation-slots")
public class ReservationSlotController {

    private final ReservationSlotService reservationSlotService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReservationSlotResponse>> createSingleSlot(
            @RequestHeader(name = "X-User-Email") String userId,
            @RequestHeader(name = "X-User-Role") String userRole,
            @PathVariable(name = "storeId") UUID storeId,
            @Valid @RequestBody CreateReservationSlotRequest request) {
        return ResponseEntity.ok(ApiResponse.success(reservationSlotService.createSingleSlot(storeId, request.toDto(), userId, userRole)));
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<String>> createBatchSlots(
            @RequestHeader(name = "X-User-Email") String userId,
            @RequestHeader(name = "X-User-Role") String userRole,
            @PathVariable(name = "storeId") UUID storeId,
            @Valid @RequestBody BatchReservationSlotsRequest request) {
        reservationSlotService.createBatchSlots(storeId, request.toDto(), userId, userRole);
        return ResponseEntity.ok(ApiResponse.success("생성을 완료하였습니다."));
    }

    @PutMapping("/{slotId}")
    public ResponseEntity<ApiResponse<ReservationSlotResponse>> updateSlot(
            @RequestHeader(name = "X-User-Email") String userId,
            @RequestHeader(name = "X-User-Role") String userRole,
            @PathVariable(name = "storeId") UUID storeId,
            @PathVariable(name = "slotId") UUID slotId,
            @RequestBody UpdateReservationSlotRequest request) {
        return ResponseEntity.ok(ApiResponse.success(reservationSlotService.updateSlot(storeId, slotId, request.toDto(), userId, userRole)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReservationSlotResponse>>> getSlotsByStore(@PathVariable(name = "storeId") UUID storeId,
                                                                                      @RequestParam(defaultValue = "1") int page,
                                                                                      @RequestParam(defaultValue = "10") int size,
                                                                                      @RequestParam(defaultValue = "createdAt") String sortBy,
                                                                                      @RequestParam(defaultValue = "false") boolean isAsc) {
        Pageable pageable = PageableUtil.createPageableWithSorting(page, size, sortBy, isAsc);
        return ResponseEntity.ok(ApiResponse.success(reservationSlotService.getSlotsByStore(storeId, pageable)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<GetReservationSlotResponse>> getSlotByDateAndTime(@PathVariable(name = "storeId") UUID storeId,
                                                                                        @RequestParam LocalDate date,
                                                                                        @RequestParam LocalTime time) {
        return ResponseEntity.ok(ApiResponse.success(reservationSlotService.getSlotByDateAndTime(storeId, date, time)));
    }

    @DeleteMapping("/{slotId}")
    public ResponseEntity<ApiResponse<String>> deleteSlot(
            @RequestHeader(name = "X-User-Email") String userId,
            @RequestHeader(name = "X-User-Role") String userRole,
            @PathVariable(name = "storeId") UUID storeId,
            @PathVariable(name = "slotId") UUID slotId) {
        reservationSlotService.deleteSlot(storeId, slotId, userId, userRole);
        return ResponseEntity.ok(ApiResponse.success("삭제가 완료되었습니다."));
    }


}
