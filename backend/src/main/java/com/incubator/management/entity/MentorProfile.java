package com.incubator.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private User user;

    @Column(name = "expertise_area")
    private String expertiseArea;

    private String organization;

    @Column(name = "years_experience")
    private Integer yearsExperience;

    @Column(columnDefinition = "TEXT")
    private String bio;
}
