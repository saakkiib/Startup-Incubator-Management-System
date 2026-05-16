package com.incubator.management.controller;

import com.incubator.management.dto.UserResponse;
import com.incubator.management.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * User Controller
 * ---------------
 * Handles all user profile management API endpoints.
 * Base URL: /api/users
 *
 * Endpoints:
 *   PUT /api/users/{id}  → Update a user's profile by ID
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Constructor injection — replaces Lombok @RequiredArgsConstructor
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Update user profile (name, phone, etc.)
     * The user ID is extracted from the URL path.
     * New profile data is sent in the request body.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateProfile(
            @PathVariable Long id,
            @RequestBody UserResponse request) {
        return ResponseEntity.ok(userService.updateProfile(id, request));
    }
}
