package com.quit.payment.infrastructure.client;

import com.quit.payment.common.dto.ApiResponse;
import com.quit.payment.infrastructure.configuration.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "reservation", url = "${reservation.base-url}", configuration = FeignClientConfig.class)
public interface ReservationClient {

    @GetMapping(value = "/api/reservations/{reservationId}/find")
    ApiResponse<UUID> getReservation(@PathVariable(name = "reservationId") UUID reservationId);

}
