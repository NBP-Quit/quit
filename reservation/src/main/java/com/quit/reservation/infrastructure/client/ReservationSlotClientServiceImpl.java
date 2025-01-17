package com.quit.reservation.infrastructure.client;

import com.quit.reservation.application.service.ReservationSlotClientService;
import com.quit.reservation.common.dto.ApiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Slf4j
@Component
public class ReservationSlotClientServiceImpl implements ReservationSlotClientService {

    private final StoreReservationSlotClient storeReservationSlotClient;

    public ReservationSlotClientServiceImpl(StoreReservationSlotClient storeReservationSlotClient) {
        this.storeReservationSlotClient = storeReservationSlotClient;
    }

    @Override
    @CircuitBreaker(name = "reservationSlotService", fallbackMethod = "fallbackGetSlotByDateAndTime")
    public ApiResponse<GetReservationSlotResponse> getSlotByDateAndTime(UUID storeId, LocalDate date, LocalTime time) {
        return storeReservationSlotClient.getSlotByDateAndTime(storeId, date, time);
    }

    public ApiResponse<GetReservationSlotResponse> fallbackGetSlotByDateAndTime(UUID storeId, LocalDate date, LocalTime time, Throwable throwable) {
        log.error("Fallback triggered due to: {}", throwable.getMessage());
        return ApiResponse.error(404, "Reservation slot service is temporarily unavailable.");
    }
}
