package com.quit.review.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.quit.review.common.ApiResponse;
import com.quit.review.infrastructure.client.ReservationClient;
import com.quit.review.infrastructure.client.ReservationResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

	private final ReservationClient reservationClient;

	@Override
	public ReservationResponse getById(UUID reservationId) {
		ApiResponse<ReservationResponse> response = reservationClient.getById(reservationId);
		if (response.getCode() != 200) {
			throw new NullPointerException("Reservation not found");
		}
		return response.getData();
	}
}
