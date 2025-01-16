package com.quit.review.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.quit.review.common.ApiResponse;
import com.quit.review.infrastructure.configuration.FeignConfig;

@FeignClient(name = "reservation", configuration = FeignConfig.class)
public interface ReservationClient {
	@GetMapping("/api/reservations/{reservationId}")
	ApiResponse<ReservationResponse> getById(@PathVariable UUID reservationId);
}
