package com.incubator.management.controller;

import com.incubator.management.dto.UserResponse;
import com.incubator.management.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * User Controller
 * ---------------
 * Handles all user profile management API endpoints.
 * Base URL: /api/users
 *
 * Endpoints:
 *   GET  /api/users               → Get all users (Admin)
 *   GET  /api/users/role/{role}   → Get users by role (Admin — e.g. list mentors)
 *   PUT  /api/users/{id}          → Update a user's profile by ID
 *   PUT  /api/users/{id}/deactivate → Deactivate a user account (Admin)
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get all users in the system.
     * Used by admin manage-users page.
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Get all users with a specific role.
     * Used by admin assign-mentors page to populate the mentor dropdown.
     * Example: GET /api/users/role/MENTOR
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    /**
     * Get a user's profile by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Update user profile (name, phone, etc.)
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateProfile(
            @PathVariable Long id,
            @RequestBody UserResponse request) {
        return ResponseEntity.ok(userService.updateProfile(id, request));
    }

    /**
     * Send a request to Admin to allow profile edits.
     */
    @PutMapping("/{id}/request-edit")
    public ResponseEntity<UserResponse> requestEdit(@PathVariable Long id) {
        return ResponseEntity.ok(userService.requestEdit(id));
    }

    /**
     * Approve profile edits (Admin endpoint).
     */
    @PutMapping("/{id}/approve-edit")
    public ResponseEntity<UserResponse> approveEdit(@PathVariable Long id) {
        return ResponseEntity.ok(userService.approveEdit(id));
    }

    /**
     * Reject profile edit request (Admin endpoint).
     */
    @PutMapping("/{id}/reject-edit")
    public ResponseEntity<UserResponse> rejectEdit(@PathVariable Long id) {
        return ResponseEntity.ok(userService.rejectEdit(id));
    }

    /**
     * Deactivate a user account (soft delete).
     * The account is disabled but kept in the database for audit purposes.
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }

    /**
     * Reactivate a user account.
     */
    @PutMapping("/{id}/reactivate")
    public ResponseEntity<UserResponse> reactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.reactivateUser(id));
    }

    /**
     * Permanently delete a user and all their related records.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Block a user account (soft block, not deletion).
     * Blocked users cannot log in but their data remains.
     */
    @PutMapping("/{id}/block")
    public ResponseEntity<UserResponse> blockUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.blockUser(id));
    }

    /**
     * Unblock a user account.
     */
    @PutMapping("/{id}/unblock")
    public ResponseEntity<UserResponse> unblockUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.unblockUser(id));
    }

    /**
     * Get all blocked users.
     */
    @GetMapping("/blocked")
    public ResponseEntity<List<UserResponse>> getBlockedUsers() {
        return ResponseEntity.ok(userService.getBlockedUsers());
    }

    /**
     * Get edit history for a user.
     */
    @GetMapping("/{id}/edit-logs")
    public ResponseEntity<List<Map<String, Object>>> getEditLogs(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getEditLogs(id));
    }

    /**
     * Get activity log for a user.
     */
    @GetMapping("/{id}/activities")
    public ResponseEntity<List<Map<String, Object>>> getActivities(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getActivities(id));
    }
}
