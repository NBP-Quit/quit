package com.quit.payment.infrastructure.client;

import com.quit.payment.common.dto.ApiResponse;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

public interface ReservationGateway {
    ApiResponse<UUID> getReservation(@PathVariable(name = "reservationId") UUID reservationId);
}
