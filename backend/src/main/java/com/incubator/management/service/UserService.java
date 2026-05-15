package com.incubator.management.service;

import com.incubator.management.dto.RegisterRequest;
import com.incubator.management.dto.UserResponse;
import com.incubator.management.entity.User;
import com.incubator.management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * User Service
 * ------------
 * Contains all business logic related to users:
 * - Registering new users
 * - Automatically creating role-based profiles
 * - Updating profile information
 * - Looking up users by email
 * - Converting User entities to UserResponse DTOs
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final InvestorProfileRepository investorProfileRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new user in the system.
     * Steps:
     * 1. Check for duplicate email
     * 2. Build and save the User entity
     * 3. Create a blank role-specific profile (Student, Mentor, or Investor)
     *
     * @param request Registration form data (name, email, password, role)
     * @return UserResponse DTO (safe — no password included)
     */
    public UserResponse register(RegisterRequest request) {
        // Reject registration if email is already taken
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Build the new User object — password is hashed before saving
        User user = User.builder()
                .username(request.getName().toLowerCase().replace(" ", "_")) // e.g. "John Doe" → "john_doe"
                .fullName(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))     // BCrypt hash for security
                .role(request.getRole())
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);

        // Create a blank profile matching the user's role — they can fill it in later
        if (savedUser.getRole() == User.Role.STUDENT) {
            studentProfileRepository.save(
                    com.incubator.management.entity.StudentProfile.builder().user(savedUser).build()
            );
        } else if (savedUser.getRole() == User.Role.MENTOR) {
            mentorProfileRepository.save(
                    com.incubator.management.entity.MentorProfile.builder().user(savedUser).build()
            );
        } else if (savedUser.getRole() == User.Role.INVESTOR) {
            investorProfileRepository.save(
                    com.incubator.management.entity.InvestorProfile.builder().user(savedUser).build()
            );
        }

        return mapToResponse(savedUser);
    }

    /**
     * Find a user by their email address.
     * Returns Optional — caller must handle the empty case.
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Update a user's profile information (name and phone only).
     * Other fields like email and role cannot be changed through this method.
     */
    public UserResponse updateProfile(Long id, UserResponse request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());

        return mapToResponse(userRepository.save(user));
    }

    /**
     * Convert a User entity into a safe UserResponse DTO.
     * This ensures the password hash is NEVER sent back to the client.
     */
    public UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .isActive(user.isActive())
                .build();
    }
}
