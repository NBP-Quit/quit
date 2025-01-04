package com.quit.store.presentation.controller;

import com.quit.store.application.dto.res.ReservationSlotResponse;
import com.quit.store.application.service.ReservationSlotService;
import com.quit.store.common.dto.ApiResponse;
import com.quit.store.common.util.PageableUtil;
import com.quit.store.presentation.dto.CreateReservationSlotRequest;
import com.quit.store.presentation.dto.UpdateReservationSlotRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
    public ResponseEntity<ApiResponse<ReservationSlotResponse>> createSlot(@PathVariable(name = "storeId") UUID storeId,
            @Valid @RequestBody CreateReservationSlotRequest request) {

        // todo:
        //  1. 게이트웨이 구현 후 @RequestHeader(name = "X-user-id") String userId 로 수정
        //  2. @RequestHeader(name = "X-user-role") String userRole 추가
        String userId = "testUserId";
        return ResponseEntity.ok(ApiResponse.success(reservationSlotService.createSlot(storeId, request.toDto(), userId)));
    }

    @PutMapping("/{slotId}")
    public ResponseEntity<ApiResponse<ReservationSlotResponse>> updateSlot(@PathVariable(name = "storeId") UUID storeId,
                                                                           @PathVariable(name = "slotId") UUID slotId,
                                                                           @RequestBody UpdateReservationSlotRequest request) {
        // todo:
        //  1. 게이트웨이 구현 후 @RequestHeader(name = "X-user-id") String userId 로 수정
        //  2. @RequestHeader(name = "X-user-role") String userRole 추가
        String userId = "testUserId";
        return ResponseEntity.ok(ApiResponse.success(reservationSlotService.updateSlot(storeId, slotId, request.toDto(), userId)));
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
    public ResponseEntity<ApiResponse<ReservationSlotResponse>> getSlotByDateAndTime(@PathVariable(name = "storeId") UUID storeId,
                                                                                     @RequestParam LocalDate date,
                                                                                     @RequestParam LocalTime time) {
        return ResponseEntity.ok(ApiResponse.success(reservationSlotService.getSlotByDateAndTime(storeId, date, time)));
    }

    @DeleteMapping("/{slotId}")
    public ResponseEntity<ApiResponse<String>> deleteSlot(@PathVariable(name = "storeId") UUID storeId,
                                                          @PathVariable(name = "slotId") UUID slotId) {
        // todo:
        //  1. 게이트웨이 구현 후 @RequestHeader(name = "X-user-id") String userId 로 수정
        //  2. @RequestHeader(name = "X-user-role") String userRole 추가
        String userId = "testUserId";
        reservationSlotService.deleteSlot(storeId, slotId, userId);
        return ResponseEntity.ok(ApiResponse.success("삭제가 완료되었습니다."));
    }


}
