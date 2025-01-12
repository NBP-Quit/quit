package com.quit.review.infrastructure.client;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReservationResponse {
	UUID reservationId;
	String customerId;
	UUID storeId;
	Integer guestCount;
	LocalDate reservationDate;
	LocalTime reservationTime;
	String reservationStatus;
	Integer reservationPrice;
}
