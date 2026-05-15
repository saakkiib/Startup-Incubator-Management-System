package com.incubator.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MentorProfile Entity
 * --------------------
 * Maps to the "mentor_profiles" table.
 * Extends the base User with mentor-specific professional details.
 * Created automatically (blank) when a user registers as MENTOR.
 */
@Entity
@Table(name = "mentor_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;                   // The mentor user this profile belongs to

    @Column(name = "expertise_area")
    private String expertiseArea;         // Domain expertise (e.g. "AI/ML", "HealthTech", "Finance")

    private String organization;          // Current employer or company name

    @Column(name = "years_experience")
    private Integer yearsExperience;      // Total years of industry experience

    @Column(columnDefinition = "TEXT")
    private String bio;                   // Professional background and mentoring philosophy
}
