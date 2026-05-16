package com.incubator.management.service;

import com.incubator.management.dto.RegisterRequest;
import com.incubator.management.dto.UserResponse;
import com.incubator.management.entity.InvestorProfile;
import com.incubator.management.entity.MentorProfile;
import com.incubator.management.entity.StudentProfile;
import com.incubator.management.entity.User;
import com.incubator.management.repository.*;
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
public class UserService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final InvestorProfileRepository investorProfileRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection — replaces Lombok @RequiredArgsConstructor
    public UserService(UserRepository userRepository,
                       StudentProfileRepository studentProfileRepository,
                       MentorProfileRepository mentorProfileRepository,
                       InvestorProfileRepository investorProfileRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.mentorProfileRepository = mentorProfileRepository;
        this.investorProfileRepository = investorProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

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

        // Build the new User object using setters — password is hashed before saving
        User user = new User();
        user.setUsername(request.getName().toLowerCase().replace(" ", "_")); // e.g. "John Doe" → "john_doe"
        user.setFullName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));     // BCrypt hash for security
        user.setRole(request.getRole());
        user.setActive(true);

        User savedUser = userRepository.save(user);

        // Create a blank profile matching the user's role — they can fill it in later
        if (savedUser.getRole() == User.Role.STUDENT) {
            StudentProfile studentProfile = new StudentProfile();
            studentProfile.setUser(savedUser);
            studentProfileRepository.save(studentProfile);

        } else if (savedUser.getRole() == User.Role.MENTOR) {
            MentorProfile mentorProfile = new MentorProfile();
            mentorProfile.setUser(savedUser);
            mentorProfileRepository.save(mentorProfile);

        } else if (savedUser.getRole() == User.Role.INVESTOR) {
            InvestorProfile investorProfile = new InvestorProfile();
            investorProfile.setUser(savedUser);
            investorProfileRepository.save(investorProfile);
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
     * Uses setters instead of @Builder.
     */
    public UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhone());
        response.setActive(user.isActive());
        return response;
    }
}
