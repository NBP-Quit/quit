package com.quit.review.application.service;

import java.util.UUID;

import com.quit.review.infrastructure.client.ReservationResponse;

public interface ReservationService {
	ReservationResponse getById(UUID reservationId);
}
