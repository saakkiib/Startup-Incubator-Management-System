package com.incubator.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * StudentProfile Entity
 * ----------------------
 * Maps to the "student_profiles" table.
 * Extends the base User with student-specific details.
 * Created automatically (blank) when a user registers as STUDENT.
 */
@Entity
@Table(name = "student_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;              // The student user this profile belongs to

    private String university;      // Name of the student's university

    private String department;      // Department or faculty (e.g. "Computer Science")

    @Column(name = "batch_year")
    private Integer batchYear;      // Graduation year (e.g. 2025)

    @Column(columnDefinition = "TEXT")
    private String bio;             // A short bio or introduction written by the student
}
