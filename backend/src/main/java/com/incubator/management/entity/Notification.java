package com.incubator.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Notification Entity
 * -------------------
 * Maps to the "notifications" table.
 * Represents an in-app notification sent to a user
 * when a key event occurs (e.g. startup approved, mentor assigned).
 */
@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;                    // The user who receives this notification

    @Column(nullable = false)
    private String title;                  // Short notification title (e.g. "Startup Approved!")

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;                // Full notification body text

    private String type;                   // Category: "approval", "funding", "assignment", etc.

    @Column(name = "is_read")
    private boolean isRead = false;        // false = unread (shown in notification badge)

    @Column(name = "related_id")
    private Long relatedId;                // ID of the related entity (e.g. startupId or fundingId)

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;       // When this notification was created
}
