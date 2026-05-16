package com.incubator.management.controller;

import com.incubator.management.entity.Notification;
import com.incubator.management.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Notification Controller
 * -----------------------
 * Handles all notification-related API endpoints.
 * Base URL: /api/notifications
 *
 * Endpoints:
 *   GET /api/notifications/user/{userId} → Fetch all notifications for a user
 *   PUT /api/notifications/{id}/read     → Mark a notification as read
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    // Constructor injection — replaces Lombok @RequiredArgsConstructor
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Fetch all notifications for a specific user (sorted newest first).
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUser(userId));
    }

    /**
     * Mark a specific notification as read using its ID.
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
