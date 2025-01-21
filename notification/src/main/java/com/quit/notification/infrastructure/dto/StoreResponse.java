package com.quit.notification.infrastructure.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class StoreResponse {
	private UUID id;
	private String name;
	private String description;
	private String address;
	private String contactNumber;
	private Integer reservationDeposit;
	private LocalTime openTime;
	private LocalTime closeTime;
	private LocalTime lastOrderTime;
	private String category;
	private LocalDateTime createdAt;
	private String createdBy;
	private LocalDateTime updatedAt;
	private String updatedBy;
}
