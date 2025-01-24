package com.quit.reservation.infrastructure.client;

import com.quit.reservation.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@FeignClient(name = "store", url = "${FEIGN_URL}")
public interface StoreClient {

    @GetMapping("/api/stores/{storeId}/reservation-slots/search")
    ApiResponse<GetReservationSlotResponse> getSlotByDateAndTime(@PathVariable("storeId") UUID storeId,
                                                                 @RequestParam LocalDate date,
                                                                 @RequestParam LocalTime time);

    @GetMapping("/api/stores/{storeId}/internal/ownership/{userId}")
    ApiResponse<Boolean> checkStoreOwnerShip(@PathVariable("storeId") UUID storeId,
                                             @PathVariable("userId") String userId);
}
