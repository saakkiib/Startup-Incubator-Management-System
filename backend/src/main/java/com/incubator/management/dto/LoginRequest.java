package com.incubator.management.dto;

/**
 * Login Request DTO
 * -----------------
 * Data Transfer Object used to receive login credentials from the client.
 * Maps to the JSON body of POST /api/auth/login.
 */
public class LoginRequest {

    private String email;     // The user's registered email address
    private String password;  // Plain-text password — compared against the stored hash in UserService

    // ─── Constructors ─────────────────────────────────────────────────────────

    public LoginRequest() {}

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // ─── Setters ──────────────────────────────────────────────────────────────

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
