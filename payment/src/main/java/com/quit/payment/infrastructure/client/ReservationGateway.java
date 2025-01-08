package com.quit.payment.infrastructure.client;

import com.quit.payment.common.dto.ApiResponse;
import com.quit.payment.infrastructure.dto.ReservationResponse;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

public interface ReservationGateway {
    ApiResponse<ReservationResponse> getReservation(@PathVariable(name = "reservationId") UUID reservationId);
}
