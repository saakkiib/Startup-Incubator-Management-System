package com.incubator.management.dto;

import com.incubator.management.entity.User;
import lombok.Data;

/**
 * Register Request DTO
 * --------------------
 * Data Transfer Object used to receive new user registration data from the client.
 * Maps to the JSON body of POST /api/auth/register.
 *
 * Fields are validated in UserService before saving to the database.
 */
@Data
public class RegisterRequest {
    private String name;       // Full name of the new user (e.g. "John Doe")
    private String email;      // Email address — must be unique in the system
    private String password;   // Plain-text password — will be hashed before storage
    private User.Role role;    // User's role: STUDENT, MENTOR, INVESTOR, or ADMIN
}
