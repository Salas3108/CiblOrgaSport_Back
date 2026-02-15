package com.ciblorgasport.notificationsservice.notification.repository;

import com.ciblorgasport.notificationsservice.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
}
