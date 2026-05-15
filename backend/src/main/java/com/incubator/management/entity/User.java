package com.incubator.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

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
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
