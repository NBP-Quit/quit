package com.quit.notification.domain.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.quit.notification.domain.model.Notification;

public interface NotificationRedisRepository extends CrudRepository<Notification, UUID> {
}
