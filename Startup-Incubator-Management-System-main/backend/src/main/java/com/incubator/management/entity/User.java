package com.incubator.management.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * User Entity
 * -----------
 * Maps to the "users" table in the database.
 * Stores core authentication info and links to role-specific profiles.
 * <p>
 * Relationships:
 * - One User → One StudentProfile  (if role = STUDENT)
 * - One User → One MentorProfile   (if role = MENTOR)
 * - One User → One InvestorProfile (if role = INVESTOR)
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;                  // Auto-generated unique user ID


    @Column(unique = true, nullable = false)
    private String username;           // Unique login handle (e.g. "john_doe")

    @Column(unique = true, nullable = false)
    private String email;              // Must be unique across all users

    @Column(name = "password", nullable = false)
    private String password;           // BCrypt-hashed password — never stored as plaintext

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(50)")
    private Role role;                 // Determines which profile and dashboard the user sees

    @Column(name = "name")
    private String fullName;           // Display name (e.g. "John Doe")

    @Column(name = "contact_no")
    private String phone;              // Optional contact number

    @Column(name = "photo", columnDefinition = "LONGTEXT")
    private String photo;

    @Column(name = "age")
    private Integer age;

    @Column(name = "sex")
    private String sex;

    @Column(name = "nid_no")
    private String nidNo;

    @Column(name = "address")
    private String address;

    @Column(name = "degree")
    private String degree;

    @Column(name = "education")
    private String education;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "expertise")
    private String expertise;

    @Column(name = "nid_certificate", columnDefinition = "LONGTEXT")
    private String nidCertificate;

    @Column(name = "academic_certificate", columnDefinition = "LONGTEXT")
    private String academicCertificate;

    @Column(name = "edit_request_status")
    private String editRequestStatus = "none";

    @Column(name = "is_super_admin")
    private boolean isSuperAdmin = false; // Super admin accounts cannot be deleted/blocked/deactivated

    @Column(name = "is_active")
    private boolean isActive = true;   // Can be set to false to deactivate an account without deleting

    @Column(name = "is_blocked")
    private boolean isBlocked = false; // Can be set to true to block a user without deleting them

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;   // Automatically set on INSERT, never changes

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;   // Automatically updated on every UPDATE

    @Version
    private int version;

    // ─── Role-Specific Profile Relationships ──────────────────────────────────
    // Only one of these will be populated, based on the user's role.

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private StudentProfile studentProfile;   // Extra data for STUDENT users

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private MentorProfile mentorProfile;     // Extra data for MENTOR users

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @com.fasterxml.jackson.annotation.JsonIgnore
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
    public User() {
    }

    // All-args constructor for convenience
    public User(Long id,
                String username,
                String email,
                String password,
                Role role,
                String fullName,
                String phone,
                boolean isActive,
                LocalDateTime createdAt,
                LocalDateTime updatedAt,
                StudentProfile studentProfile,
                MentorProfile mentorProfile,
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

    public String getPhoto() {
        return photo;
    }

    public Integer getAge() {
        return age;
    }

    public String getSex() {
        return sex;
    }

    public String getNidNo() {
        return nidNo;
    }

    public String getAddress() {
        return address;
    }

    public String getDegree() {
        return degree;
    }

    public String getEducation() {
        return education;
    }

    public String getBio() {
        return bio;
    }

    public String getExpertise() {
        return expertise;
    }

    public String getNidCertificate() {
        return nidCertificate;
    }

    public String getAcademicCertificate() {
        return academicCertificate;
    }

    public String getEditRequestStatus() {
        return editRequestStatus;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isSuperAdmin() {
        return isSuperAdmin;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
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

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setNidNo(String nidNo) {
        this.nidNo = nidNo;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public void setNidCertificate(String nidCertificate) {
        this.nidCertificate = nidCertificate;
    }

    public void setAcademicCertificate(String academicCertificate) {
        this.academicCertificate = academicCertificate;
    }

    public void setEditRequestStatus(String editRequestStatus) {
        this.editRequestStatus = editRequestStatus;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setSuperAdmin(boolean superAdmin) {
        isSuperAdmin = superAdmin;
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
