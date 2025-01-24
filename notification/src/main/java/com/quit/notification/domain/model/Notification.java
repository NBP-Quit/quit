package com.quit.notification.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@RedisHash(value = "notification", timeToLive = 604800)
public class Notification {

	@Id
	private UUID id;

	private String content;

	private Status status;

	private LocalDateTime createdAt;

	public static Notification create(UUID id, String content, Status status, LocalDateTime createdAt) {
		return Notification.builder()
			.id(id)
			.content(content)
			.status(status)
			.createdAt(createdAt)
			.build();
	}
}
