package com.incubator.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

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
    private Startup startup;

    @ManyToOne
    @JoinColumn(name = "mentor_id", nullable = false)
    private User mentor;

    @ManyToOne
    @JoinColumn(name = "assigned_by", nullable = false)
    private User assignedBy;

    @CreationTimestamp
    @Column(name = "assigned_at", updatable = false)
    private LocalDateTime assignedAt;

    private String status = "active";

    @Column(columnDefinition = "TEXT")
    private String notes;
}
