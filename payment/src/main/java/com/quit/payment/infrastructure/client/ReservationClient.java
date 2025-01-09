package com.quit.payment.infrastructure.client;

import com.quit.payment.common.dto.ApiResponse;
import com.quit.payment.infrastructure.dto.ReservationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "reservation", url = "http://localhost:19094")
public interface ReservationClient {

    @GetMapping(value = "/api/reservations/{reservationId}")
    ApiResponse<ReservationResponse> getReservation(@PathVariable(name = "reservationId") UUID reservationId);

}
