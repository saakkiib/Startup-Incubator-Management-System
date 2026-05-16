package com.incubator.management.dto;

import com.incubator.management.entity.User;

/**
 * User Response DTO
 * -----------------
 * Safe response object returned to the client after auth and profile operations.
 * IMPORTANT: The password hash is intentionally excluded from this DTO.
 * Built by UserService.mapToResponse().
 */
public class UserResponse {

    private Long id;           // Unique user ID (used for subsequent API calls)
    private String username;   // Unique login handle
    private String email;      // User's email address
    private User.Role role;    // User's role — determines their dashboard and permissions
    private String fullName;   // Display name shown in the UI
    private String phone;      // Contact phone number (optional)
    private boolean isActive;  // Whether the account is active or deactivated

    // ─── Constructors ─────────────────────────────────────────────────────────

    public UserResponse() {}

    public UserResponse(Long id, String username, String email, User.Role role,
                        String fullName, String phone, boolean isActive) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.fullName = fullName;
        this.phone = phone;
        this.isActive = isActive;
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

    public User.Role getRole() {
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

    public void setRole(User.Role role) {
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
}
