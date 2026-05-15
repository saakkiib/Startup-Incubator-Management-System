package com.incubator.management.service;

import com.incubator.management.entity.Notification;
import com.incubator.management.entity.User;
import com.incubator.management.repository.NotificationRepository;
import com.incubator.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Notification Service
 * --------------------
 * Handles all business logic for notifications:
 * - Creating notifications for users (called internally by other services)
 * - Fetching all notifications for a user (newest first)
 * - Marking a notification as read
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * Create and save a new notification for a specific user.
     * This is typically called internally when a key event occurs
     * (e.g. startup approved, mentor assigned, funding received).
     *
     * @param userId    The recipient user's ID
     * @param title     Short notification title
     * @param message   Full notification message
     * @param type      Notification category (e.g. "approval", "funding")
     * @param relatedId ID of the related entity (e.g. startupId, fundingId)
     */
    public Notification createNotification(Long userId, String title, String message, String type, Long relatedId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .relatedId(relatedId)
                .isRead(false) // All new notifications start as unread
                .build();

        return notificationRepository.save(notification);
    }

    /**
     * Retrieve all notifications for a user, sorted by newest first.
     */
    public List<Notification> getNotificationsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * Mark a specific notification as read.
     * Used when the user opens or acknowledges a notification.
     */
    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
