package com.incubator.management.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * MentorAssignment Entity
 * -----------------------
 * Maps to the "mentor_assignments" table.
 * Records the assignment of a mentor to a startup — performed by an Admin.
 * A startup can have multiple mentor assignments over time.
 */
@Entity
@Table(name = "mentor_assignments")
public class MentorAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "startup_id", nullable = false)
    @JsonIgnoreProperties({"founder", "studentProfile", "mentorProfile", "investorProfile", "password"})
    private Startup startup;              // The startup being mentored

    @ManyToOne
    @JoinColumn(name = "mentor_id", nullable = false)
    @JsonIgnoreProperties({"studentProfile", "mentorProfile", "investorProfile", "password"})
    private User mentor;                  // The mentor assigned to guide the startup

    @ManyToOne
    @JoinColumn(name = "assigned_by", nullable = false)
    @JsonIgnoreProperties({"studentProfile", "mentorProfile", "investorProfile", "password"})
    private User assignedBy;              // The admin who created this assignment

    @CreationTimestamp
    @Column(name = "assigned_at", updatable = false)
    private LocalDateTime assignedAt;     // When the assignment was created

    private String status = "active";     // "active" or "completed" or "cancelled"

    @Column(columnDefinition = "TEXT")
    private String notes;                 // Optional admin notes about the assignment

    // ─── Constructors ─────────────────────────────────────────────────────────

    // Default (no-arg) constructor — required by JPA
    public MentorAssignment() {}

    // All-args constructor for convenience
    public MentorAssignment(Long id, Startup startup, User mentor, User assignedBy,
                            LocalDateTime assignedAt, String status, String notes) {
        this.id = id;
        this.startup = startup;
        this.mentor = mentor;
        this.assignedBy = assignedBy;
        this.assignedAt = assignedAt;
        this.status = status;
        this.notes = notes;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public Startup getStartup() {
        return startup;
    }

    public User getMentor() {
        return mentor;
    }

    public User getAssignedBy() {
        return assignedBy;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public String getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    // ─── Setters ──────────────────────────────────────────────────────────────

    public void setId(Long id) {
        this.id = id;
    }

    public void setStartup(Startup startup) {
        this.startup = startup;
    }

    public void setMentor(User mentor) {
        this.mentor = mentor;
    }

    public void setAssignedBy(User assignedBy) {
        this.assignedBy = assignedBy;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // ─── toString ─────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "MentorAssignment{" +
                "id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}
