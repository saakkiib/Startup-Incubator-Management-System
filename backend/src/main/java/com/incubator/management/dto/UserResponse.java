package com.incubator.management.dto;

import com.incubator.management.entity.User;
import lombok.Builder;
import lombok.Data;

/**
 * User Response DTO
 * -----------------
 * Safe response object returned to the client after auth and profile operations.
 * IMPORTANT: The password hash is intentionally excluded from this DTO.
 * Built by UserService.mapToResponse().
 */
@Data
@Builder
public class UserResponse {
    private Long id;           // Unique user ID (used for subsequent API calls)
    private String username;   // Unique login handle
    private String email;      // User's email address
    private User.Role role;    // User's role — determines their dashboard and permissions
    private String fullName;   // Display name shown in the UI
    private String phone;      // Contact phone number (optional)
    private boolean isActive;  // Whether the account is active or deactivated
}
