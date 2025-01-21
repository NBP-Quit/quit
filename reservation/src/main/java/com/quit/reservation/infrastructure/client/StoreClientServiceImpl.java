package com.quit.reservation.infrastructure.client;

import com.quit.reservation.application.service.StoreClientService;
import com.quit.reservation.common.dto.ApiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Slf4j
@Component
public class StoreClientServiceImpl implements StoreClientService {

    private final StoreClient storeClient;

    public StoreClientServiceImpl(StoreClient storeClient) {
        this.storeClient = storeClient;
    }

    @Override
    @CircuitBreaker(name = "StoreClientService", fallbackMethod = "fallbackGetSlotByDateAndTime")
    public ApiResponse<GetReservationSlotResponse> getSlotByDateAndTime(UUID storeId, LocalDate date, LocalTime time) {
        return storeClient.getSlotByDateAndTime(storeId, date, time);
    }

    @Override
    @CircuitBreaker(name = "StoreClientService", fallbackMethod = "fallbackCheckStoreOwnership")
    public ApiResponse<Boolean> checkStoreOwnership(UUID storeId, String ownerId) {
        return storeClient.checkStoreOwnerShip(storeId, ownerId);
    }

    public ApiResponse<GetReservationSlotResponse> fallbackGetSlotByDateAndTime(UUID storeId, LocalDate date, LocalTime time, Throwable throwable) {
        errorLog(throwable);
        return ApiResponse.error(404, "Store Service is temporarily unavailable.");
    }

    public ApiResponse<Boolean> fallbackCheckStoreOwnership(UUID storeId, String ownerId, Throwable throwable) {
        errorLog(throwable);
        return ApiResponse.error(404, "Store service is temporarily unavailable");
    }

    private void errorLog(Throwable throwable) {
        log.error("Fallback triggered due to: {}", throwable.getMessage());
    }
}
