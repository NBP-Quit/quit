package com.quit.reservation.application.service;

import com.quit.reservation.common.dto.ApiResponse;
import com.quit.reservation.infrastructure.client.GetReservationSlotResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public interface ReservationSlotClientService {
    ApiResponse<GetReservationSlotResponse> getSlotByDateAndTime(UUID storeId, LocalDate date, LocalTime time);
}
