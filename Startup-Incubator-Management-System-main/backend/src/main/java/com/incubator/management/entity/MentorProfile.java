package com.incubator.management.entity;

import jakarta.persistence.*;

/**
 * MentorProfile Entity
 * --------------------
 * Maps to the "mentor_profiles" table.
 * Extends the base User with mentor-specific professional details.
 * Created automatically (blank) when a user registers as MENTOR.
 */
@Entity
@Table(name = "mentor_profiles")
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

    // ─── Constructors ─────────────────────────────────────────────────────────

    // Default (no-arg) constructor — required by JPA
    public MentorProfile() {}

    // All-args constructor for convenience
    public MentorProfile(Long id, User user, String expertiseArea,
                         String organization, Integer yearsExperience, String bio) {
        this.id = id;
        this.user = user;
        this.expertiseArea = expertiseArea;
        this.organization = organization;
        this.yearsExperience = yearsExperience;
        this.bio = bio;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getExpertiseArea() {
        return expertiseArea;
    }

    public String getOrganization() {
        return organization;
    }

    public Integer getYearsExperience() {
        return yearsExperience;
    }

    public String getBio() {
        return bio;
    }

    // ─── Setters ──────────────────────────────────────────────────────────────

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setExpertiseArea(String expertiseArea) {
        this.expertiseArea = expertiseArea;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setYearsExperience(Integer yearsExperience) {
        this.yearsExperience = yearsExperience;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    // ─── toString ─────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "MentorProfile{" +
                "id=" + id +
                ", expertiseArea='" + expertiseArea + '\'' +
                ", organization='" + organization + '\'' +
                ", yearsExperience=" + yearsExperience +
                '}';
    }
}
