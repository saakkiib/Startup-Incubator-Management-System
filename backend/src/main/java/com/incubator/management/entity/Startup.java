package com.incubator.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Startup Entity
 * --------------
 * Maps to the "startups" table in the database.
 * Represents a startup project submitted by a student/founder.
 *
 * Lifecycle of a startup:
 *   "pending" → (Admin reviews) → "approved" or "rejected"
 */
@Entity
@Table(name = "startups")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Startup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "startup_id")
    private Long id;                // Auto-generated unique ID

    @ManyToOne
    @JoinColumn(name = "founder_id", nullable = false)
    private User founder;            // The user who created this startup (must be STUDENT role)

    @Column(nullable = false)
    private String name;             // Name of the startup (e.g. "EcoPack Solutions")

    @Column(columnDefinition = "TEXT")
    private String description;      // Detailed description of what the startup does

    private String industry;         // Industry sector (e.g. "HealthTech", "FinTech", "EdTech")

    private String stage;            // Current development stage (e.g. "Idea", "MVP", "Growth")

    @Column(nullable = false)
    private String status = "pending"; // Approval status: "pending", "approved", or "rejected"

    @CreationTimestamp
    @Column(name = "submitted_at", updatable = false)
    private LocalDateTime submittedAt; // Timestamp when the startup was first submitted

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;   // Timestamp of the last update to this record
}
