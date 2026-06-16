package com.incubator.management.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
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
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"studentProfile", "mentorProfile", "investorProfile", "password"})
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

    // ─── Constructors ─────────────────────────────────────────────────────────

    // Default (no-arg) constructor — required by JPA
    public Notification() {}

    // All-args constructor for convenience
    public Notification(Long id, User user, String title, String message,
                        String type, boolean isRead, Long relatedId,
                        LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.relatedId = relatedId;
        this.createdAt = createdAt;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public boolean isRead() {
        return isRead;
    }

    public Long getRelatedId() {
        return relatedId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // ─── Setters ──────────────────────────────────────────────────────────────

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void setRelatedId(Long relatedId) {
        this.relatedId = relatedId;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // ─── toString ─────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", isRead=" + isRead +
                '}';
    }
}
