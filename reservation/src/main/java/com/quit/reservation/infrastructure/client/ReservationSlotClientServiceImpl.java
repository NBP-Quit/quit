package com.quit.reservation.infrastructure.client;

import com.quit.reservation.application.service.ReservationSlotClientService;
import com.quit.reservation.common.dto.ApiResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Component
public class ReservationSlotClientServiceImpl implements ReservationSlotClientService {

    private final StoreReservationSlotClient storeReservationSlotClient;

    public ReservationSlotClientServiceImpl(StoreReservationSlotClient storeReservationSlotClient) {
        this.storeReservationSlotClient = storeReservationSlotClient;
    }

    @Override
    public ApiResponse<ReservationSlotResponse> getSlotByDateAndTime(UUID storeId, LocalDate date, LocalTime time) {
        return storeReservationSlotClient.getSlotByDateAndTime(storeId, date, time);
    }
}
