package com.incubator.management.entity;

import jakarta.persistence.*;
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

    // ─── Constructors ─────────────────────────────────────────────────────────

    // Default (no-arg) constructor — required by JPA
    public Startup() {}

    // All-args constructor for convenience
    public Startup(Long id, User founder, String name, String description,
                   String industry, String stage, String status,
                   LocalDateTime submittedAt, LocalDateTime updatedAt) {
        this.id = id;
        this.founder = founder;
        this.name = name;
        this.description = description;
        this.industry = industry;
        this.stage = stage;
        this.status = status;
        this.submittedAt = submittedAt;
        this.updatedAt = updatedAt;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public User getFounder() {
        return founder;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getIndustry() {
        return industry;
    }

    public String getStage() {
        return stage;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // ─── Setters ──────────────────────────────────────────────────────────────

    public void setId(Long id) {
        this.id = id;
    }

    public void setFounder(User founder) {
        this.founder = founder;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ─── toString ─────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "Startup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", industry='" + industry + '\'' +
                ", stage='" + stage + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
