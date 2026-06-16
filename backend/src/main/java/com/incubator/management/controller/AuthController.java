package com.incubator.management.controller;

import com.incubator.management.dto.LoginRequest;
import com.incubator.management.dto.RegisterRequest;
import com.incubator.management.dto.UserResponse;
import com.incubator.management.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Auth Controller
 * ---------------
 * Handles all authentication-related API endpoints.
 * Base URL: /api/auth
 *
 * Endpoints:
 *   POST /api/auth/register  → Register a new user
 *   POST /api/auth/login     → Login with existing credentials
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection — replaces Lombok @RequiredArgsConstructor
    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user account.
     * Accepts name, email, password and role in the request body.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (request.getRole() != com.incubator.management.entity.User.Role.ADMIN &&
            (request.getEmail() == null || !request.getEmail().toLowerCase().endsWith("@gmail.com"))) {
            return ResponseEntity.badRequest().body("{\"error\":\"Registration is only allowed for @gmail.com addresses.\"}");
        }
        return ResponseEntity.ok(userService.register(request));
    }

    /**
     * Login with email and password.
     * Returns user data on success, or 401 Unauthorized on failure.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return userService.findByEmail(request.getEmail())
                // Check if the provided password matches the stored hashed password
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                // Check if the user account is active (not deactivated)
                .filter(user -> user.isActive())
                // Check if the user account is blocked
                .filter(user -> !user.isBlocked())
                .map(user -> ResponseEntity.ok(userService.mapToResponse(user)))
                .orElse(ResponseEntity.status(401).build()); // Return 401 if credentials don't match
    }
}
