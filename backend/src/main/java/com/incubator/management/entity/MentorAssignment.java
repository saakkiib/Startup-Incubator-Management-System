package com.incubator.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "startup_id", nullable = false)
    private Startup startup;              // The startup being mentored

    @ManyToOne
    @JoinColumn(name = "mentor_id", nullable = false)
    private User mentor;                  // The mentor assigned to guide the startup

    @ManyToOne
    @JoinColumn(name = "assigned_by", nullable = false)
    private User assignedBy;              // The admin who created this assignment

    @CreationTimestamp
    @Column(name = "assigned_at", updatable = false)
    private LocalDateTime assignedAt;     // When the assignment was created

    private String status = "active";     // "active" or "completed" or "cancelled"

    @Column(columnDefinition = "TEXT")
    private String notes;                 // Optional admin notes about the assignment
}
