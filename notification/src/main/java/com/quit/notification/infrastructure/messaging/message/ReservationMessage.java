package com.quit.notification.infrastructure.messaging.message;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ReservationMessage {
	private UUID reservationId;
	private String customerId;
	private UUID storeId;
	private Integer guestCount;
	private LocalDate reservationDate;
	private LocalTime reservationTime;
	private ReservationEvent reservationEvent;
	private Integer reservationPrice;

}
