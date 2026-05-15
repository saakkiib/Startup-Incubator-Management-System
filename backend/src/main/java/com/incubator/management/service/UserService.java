package com.incubator.management.service;

import com.incubator.management.dto.RegisterRequest;
import com.incubator.management.dto.UserResponse;
import com.incubator.management.entity.User;
import com.incubator.management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final InvestorProfileRepository investorProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(request.getName().toLowerCase().replace(" ", "_")) // Simple username generation
                .fullName(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);

        // Create empty profile based on role
        if (savedUser.getRole() == User.Role.STUDENT) {
            studentProfileRepository.save(com.incubator.management.entity.StudentProfile.builder().user(savedUser).build());
        } else if (savedUser.getRole() == User.Role.MENTOR) {
            mentorProfileRepository.save(com.incubator.management.entity.MentorProfile.builder().user(savedUser).build());
        } else if (savedUser.getRole() == User.Role.INVESTOR) {
            investorProfileRepository.save(com.incubator.management.entity.InvestorProfile.builder().user(savedUser).build());
        }

        return mapToResponse(savedUser);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserResponse updateProfile(Long id, UserResponse request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());

        return mapToResponse(userRepository.save(user));
    }

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
