package com.incubator.management.repository;

import com.incubator.management.entity.Notification;
import com.incubator.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Notification Repository
 * -----------------------
 * Provides database access for the Notification entity.
 * JpaRepository gives standard CRUD methods out of the box.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Get all notifications for a user, sorted newest first.
     * Used to populate the notification panel in the UI.
     */
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    /**
     * Get only unread notifications for a user.
     * Used to calculate the unread badge count shown in the navbar.
     */
    List<Notification> findByUserAndIsReadFalse(User user);
}
