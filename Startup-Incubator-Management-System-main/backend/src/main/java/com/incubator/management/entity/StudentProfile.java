package com.incubator.management.entity;

import jakarta.persistence.*;

/**
 * StudentProfile Entity
 * ----------------------
 * Maps to the "student_profiles" table.
 * Extends the base User with student-specific details.
 * Created automatically (blank) when a user registers as STUDENT.
 */
@Entity
@Table(name = "student_profiles")
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

    // ─── Constructors ─────────────────────────────────────────────────────────

    // Default (no-arg) constructor — required by JPA
    public StudentProfile() {}

    // All-args constructor for convenience
    public StudentProfile(Long id, User user, String university,
                          String department, Integer batchYear, String bio) {
        this.id = id;
        this.user = user;
        this.university = university;
        this.department = department;
        this.batchYear = batchYear;
        this.bio = bio;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getUniversity() {
        return university;
    }

    public String getDepartment() {
        return department;
    }

    public Integer getBatchYear() {
        return batchYear;
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

    public void setUniversity(String university) {
        this.university = university;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setBatchYear(Integer batchYear) {
        this.batchYear = batchYear;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    // ─── toString ─────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "StudentProfile{" +
                "id=" + id +
                ", university='" + university + '\'' +
                ", department='" + department + '\'' +
                ", batchYear=" + batchYear +
                '}';
    }
}
