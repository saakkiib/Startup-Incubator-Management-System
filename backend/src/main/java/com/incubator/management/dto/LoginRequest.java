package com.incubator.management.dto;

import lombok.Data;

/**
 * Login Request DTO
 * -----------------
 * Data Transfer Object used to receive login credentials from the client.
 * Maps to the JSON body of POST /api/auth/login.
 */
@Data
public class LoginRequest {
    private String email;     // The user's registered email address
    private String password;  // Plain-text password — compared against the stored hash in UserService
}
