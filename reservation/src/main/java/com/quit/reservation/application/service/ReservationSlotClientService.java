package com.quit.reservation.application.service;

import com.quit.reservation.common.dto.ApiResponse;
import com.quit.reservation.infrastructure.client.ReservationSlotResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public interface ReservationSlotClientService {
    ApiResponse<ReservationSlotResponse> getSlotByDateAndTime(UUID storeId, LocalDate date, LocalTime time);
}
