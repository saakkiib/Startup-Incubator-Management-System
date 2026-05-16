package com.incubator.management.dto;

import com.incubator.management.entity.User;

/**
 * Register Request DTO
 * --------------------
 * Data Transfer Object used to receive new user registration data from the client.
 * Maps to the JSON body of POST /api/auth/register.
 *
 * Fields are validated in UserService before saving to the database.
 */
public class RegisterRequest {

    private String name;       // Full name of the new user (e.g. "John Doe")
    private String email;      // Email address — must be unique in the system
    private String password;   // Plain-text password — will be hashed before storage
    private User.Role role;    // User's role: STUDENT, MENTOR, INVESTOR, or ADMIN

    // ─── Constructors ─────────────────────────────────────────────────────────

    public RegisterRequest() {}

    public RegisterRequest(String name, String email, String password, User.Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public User.Role getRole() {
        return role;
    }

    // ─── Setters ──────────────────────────────────────────────────────────────

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(User.Role role) {
        this.role = role;
    }
}
