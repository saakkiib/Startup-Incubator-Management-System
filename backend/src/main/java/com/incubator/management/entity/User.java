package com.incubator.management.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * User Entity
 * -----------
 * Maps to the "users" table in the database.
 * Stores core authentication info and links to role-specific profiles.
 *
 * Relationships:
 *   - One User → One StudentProfile  (if role = STUDENT)
 *   - One User → One MentorProfile   (if role = MENTOR)
 *   - One User → One InvestorProfile (if role = INVESTOR)
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;                  // Auto-generated unique user ID

    @Column(unique = true, nullable = false)
    private String username;           // Unique login handle (e.g. "john_doe")

    @Column(unique = true, nullable = false)
    private String email;              // Must be unique across all users

    @Column(name = "password_hash", nullable = false)
    private String password;           // BCrypt-hashed password — never stored as plaintext

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;                 // Determines which profile and dashboard the user sees

    @Column(name = "full_name")
    private String fullName;           // Display name (e.g. "John Doe")

    private String phone;              // Optional contact number

    @Column(name = "is_active")
    private boolean isActive = true;   // Can be set to false to deactivate an account without deleting

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;   // Automatically set on INSERT, never changes

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;   // Automatically updated on every UPDATE

    // ─── Role-Specific Profile Relationships ──────────────────────────────────
    // Only one of these will be populated, based on the user's role.

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private StudentProfile studentProfile;   // Extra data for STUDENT users

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private MentorProfile mentorProfile;     // Extra data for MENTOR users

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private InvestorProfile investorProfile; // Extra data for INVESTOR users

    // ─── Role Enum ────────────────────────────────────────────────────────────

    /**
     * The four roles in the system.
     * Each role gets a different dashboard and set of permissions.
     */
    public enum Role {
        STUDENT,   // Entrepreneurs who submit startups
        MENTOR,    // Industry experts who guide startups
        INVESTOR,  // Funders who invest in startups
        ADMIN      // Platform administrators with full access
    }

    // ─── Constructors ─────────────────────────────────────────────────────────

    // Default (no-arg) constructor — required by JPA
    public User() {}

    // All-args constructor for convenience
    public User(Long id, String username, String email, String password,
                Role role, String fullName, String phone, boolean isActive,
                LocalDateTime createdAt, LocalDateTime updatedAt,
                StudentProfile studentProfile, MentorProfile mentorProfile,
                InvestorProfile investorProfile) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.phone = phone;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.studentProfile = studentProfile;
        this.mentorProfile = mentorProfile;
        this.investorProfile = investorProfile;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isActive() {
        return isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public StudentProfile getStudentProfile() {
        return studentProfile;
    }

    public MentorProfile getMentorProfile() {
        return mentorProfile;
    }

    public InvestorProfile getInvestorProfile() {
        return investorProfile;
    }

    // ─── Setters ──────────────────────────────────────────────────────────────

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setStudentProfile(StudentProfile studentProfile) {
        this.studentProfile = studentProfile;
    }

    public void setMentorProfile(MentorProfile mentorProfile) {
        this.mentorProfile = mentorProfile;
    }

    public void setInvestorProfile(InvestorProfile investorProfile) {
        this.investorProfile = investorProfile;
    }

    // ─── toString ─────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", fullName='" + fullName + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
