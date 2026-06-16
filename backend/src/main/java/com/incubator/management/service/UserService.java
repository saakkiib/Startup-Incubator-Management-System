package com.incubator.management.service;

import com.incubator.management.dto.RegisterRequest;
import com.incubator.management.dto.UserResponse;
import com.incubator.management.entity.*;
import com.incubator.management.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
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
    private final NotificationService notificationService;
    private final BlockedEmailRepository blockedEmailRepository;
    private final NotificationRepository notificationRepository;
    private final EvaluationRepository evaluationRepository;
    private final FundingRepository fundingRepository;
    private final MentorAssignmentRepository mentorAssignmentRepository;
    private final PitchDocumentRepository pitchDocumentRepository;
    private final StartupRepository startupRepository;
    private final EditLogRepository editLogRepository;
    private final ActivityLogRepository activityLogRepository;

    // Constructor injection — replaces Lombok @RequiredArgsConstructor
    public UserService(UserRepository userRepository,
                       StudentProfileRepository studentProfileRepository,
                       MentorProfileRepository mentorProfileRepository,
                       InvestorProfileRepository investorProfileRepository,
                       PasswordEncoder passwordEncoder,
                       NotificationService notificationService,
                       BlockedEmailRepository blockedEmailRepository,
                       NotificationRepository notificationRepository,
                       EvaluationRepository evaluationRepository,
                       FundingRepository fundingRepository,
                       MentorAssignmentRepository mentorAssignmentRepository,
                        PitchDocumentRepository pitchDocumentRepository,
                        StartupRepository startupRepository,
                        EditLogRepository editLogRepository,
                        ActivityLogRepository activityLogRepository) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.mentorProfileRepository = mentorProfileRepository;
        this.investorProfileRepository = investorProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
        this.blockedEmailRepository = blockedEmailRepository;
        this.notificationRepository = notificationRepository;
        this.evaluationRepository = evaluationRepository;
        this.fundingRepository = fundingRepository;
        this.mentorAssignmentRepository = mentorAssignmentRepository;
        this.pitchDocumentRepository = pitchDocumentRepository;
        this.startupRepository = startupRepository;
        this.editLogRepository = editLogRepository;
        this.activityLogRepository = activityLogRepository;
    }

    /**
     * Register a new user in the system.
     * Steps:
     * 1. Check for duplicate email
     * 2. Check if email is blocked
     * 3. Build and save the User entity
     * 4. Create a blank role-specific profile (Student, Mentor, or Investor)
     *
     * @param request Registration form data (name, email, password, role)
     * @return UserResponse DTO (safe — no password included)
     */
    public UserResponse register(RegisterRequest request) {
        // Reject registration if email is already taken
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Reject if email is blocked (previously deleted with block)
        if (blockedEmailRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("This email is blocked and cannot be used to register.");
        }

        // Build the new User object using setters — password is hashed before saving
        User user = new User();
        String baseUsername = request.getName().toLowerCase().replace(" ", "_");
        user.setUsername(baseUsername + "_" + System.currentTimeMillis()); // e.g. "john_doe_1684392019"
        user.setFullName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));     // BCrypt hash for security
        user.setRole(request.getRole());
        user.setActive(true);

        User savedUser = userRepository.save(user);

        // Create a blank profile matching the user's role — they can fill it in later
        if (User.Role.STUDENT.equals(savedUser.getRole())) {
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

        // Capture old values for change tracking
        StringBuilder changes = new StringBuilder();
        if (!equals(user.getFullName(), request.getFullName()))
            changes.append("name: ").append(user.getFullName()).append(" → ").append(request.getFullName()).append("; ");
        if (!equals(user.getPhone(), request.getPhone()))
            changes.append("phone: ").append(user.getPhone()).append(" → ").append(request.getPhone()).append("; ");
        if (!equals(user.getPhoto(), request.getPhoto()))
            changes.append("photo changed; ");
        if (!equals(user.getAge(), request.getAge()))
            changes.append("age: ").append(user.getAge()).append(" → ").append(request.getAge()).append("; ");
        if (!equals(user.getSex(), request.getSex()))
            changes.append("sex: ").append(user.getSex()).append(" → ").append(request.getSex()).append("; ");
        if (!equals(user.getNidNo(), request.getNidNo()))
            changes.append("nid: ").append(user.getNidNo()).append(" → ").append(request.getNidNo()).append("; ");
        if (!equals(user.getAddress(), request.getAddress()))
            changes.append("address: ").append(user.getAddress()).append(" → ").append(request.getAddress()).append("; ");
        if (!equals(user.getDegree(), request.getDegree()))
            changes.append("degree: ").append(user.getDegree()).append(" → ").append(request.getDegree()).append("; ");
        if (!equals(user.getEducation(), request.getEducation()))
            changes.append("education: ").append(user.getEducation()).append(" → ").append(request.getEducation()).append("; ");
        if (!equals(user.getBio(), request.getBio()))
            changes.append("bio changed; ");
        if (!equals(user.getExpertise(), request.getExpertise()))
            changes.append("expertise: ").append(user.getExpertise()).append(" → ").append(request.getExpertise()).append("; ");

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setPhoto(request.getPhoto());
        user.setAge(request.getAge());
        user.setSex(request.getSex());
        user.setNidNo(request.getNidNo());
        user.setAddress(request.getAddress());
        user.setDegree(request.getDegree());
        user.setEducation(request.getEducation());
        user.setBio(request.getBio());
        user.setExpertise(request.getExpertise());
        user.setNidCertificate(request.getNidCertificate());
        user.setAcademicCertificate(request.getAcademicCertificate());
        user.setEditRequestStatus("none"); // Reset edit request status upon saving

        if (user.getRole() == User.Role.INVESTOR) {
            InvestorProfile investorProfile = user.getInvestorProfile();
            if (investorProfile == null) {
                investorProfile = new InvestorProfile();
                investorProfile.setUser(user);
            }
            investorProfile.setInvestmentFocus(request.getInvestmentFocus());
            investorProfileRepository.save(investorProfile);
            user.setInvestorProfile(investorProfile);
        }

        if (user.getRole() == User.Role.STUDENT) {
            StudentProfile studentProfile = user.getStudentProfile();
            if (studentProfile == null) {
                studentProfile = new StudentProfile();
                studentProfile.setUser(user);
            }
            if (!equals(studentProfile.getUniversity(), request.getUniversity()))
                changes.append("university: ").append(studentProfile.getUniversity()).append(" → ").append(request.getUniversity()).append("; ");
            if (!equals(studentProfile.getDepartment(), request.getDepartment()))
                changes.append("department: ").append(studentProfile.getDepartment()).append(" → ").append(request.getDepartment()).append("; ");
            if (!equals(studentProfile.getBatchYear(), request.getBatchYear()))
                changes.append("batchYear: ").append(studentProfile.getBatchYear()).append(" → ").append(request.getBatchYear()).append("; ");
            studentProfile.setUniversity(request.getUniversity());
            studentProfile.setDepartment(request.getDepartment());
            studentProfile.setBatchYear(request.getBatchYear());
            user.setStudentProfile(studentProfile);
        }

        if (user.getRole() == User.Role.MENTOR) {
            MentorProfile mentorProfile = user.getMentorProfile();
            if (mentorProfile == null) {
                mentorProfile = new MentorProfile();
                mentorProfile.setUser(user);
            }
            if (!equals(mentorProfile.getExpertiseArea(), request.getExpertiseArea()))
                changes.append("expertiseArea: ").append(mentorProfile.getExpertiseArea()).append(" → ").append(request.getExpertiseArea()).append("; ");
            if (!equals(mentorProfile.getOrganization(), request.getOrganization()))
                changes.append("organization: ").append(mentorProfile.getOrganization()).append(" → ").append(request.getOrganization()).append("; ");
            if (!equals(mentorProfile.getYearsExperience(), request.getYearsExperience()))
                changes.append("yearsExperience: ").append(mentorProfile.getYearsExperience()).append(" → ").append(request.getYearsExperience()).append("; ");
            mentorProfile.setExpertiseArea(request.getExpertiseArea());
            mentorProfile.setOrganization(request.getOrganization());
            mentorProfile.setYearsExperience(request.getYearsExperience());
            mentorProfileRepository.save(mentorProfile);
            user.setMentorProfile(mentorProfile);
        }

        UserResponse response = mapToResponse(userRepository.save(user));

        // Log changes if any
        String changeStr = changes.toString().trim();
        if (!changeStr.isEmpty()) {
            if (changeStr.endsWith(";")) changeStr = changeStr.substring(0, changeStr.length() - 1);
            editLogRepository.save(new com.incubator.management.entity.EditLog(id, "PROFILE_UPDATE", changeStr));
            activityLogRepository.save(new com.incubator.management.entity.ActivityLog(id, "PROFILE_UPDATE", changeStr));
        }

        return response;
    }

    private boolean equals(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    public UserResponse requestEdit(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEditRequestStatus("pending");
        UserResponse response = mapToResponse(userRepository.save(user));
        editLogRepository.save(new com.incubator.management.entity.EditLog(id, "EDIT_REQUESTED", "User requested profile edit"));
        activityLogRepository.save(new com.incubator.management.entity.ActivityLog(id, "EDIT_REQUESTED", "Requested profile edit approval"));

        String name = user.getFullName() != null ? user.getFullName() : user.getUsername();
        String role = user.getRole() != null ? user.getRole().name() : "";
        String msg = name + " (" + role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase() + ") submitted a profile edit request.";
        List<User> admins = userRepository.findByRole(User.Role.ADMIN);
        for (User admin : admins) {
            notificationService.createNotification(admin.getId(), "Profile Edit Request", msg, "EDIT_REQUEST", user.getId());
        }

        return response;
    }

    public UserResponse approveEdit(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEditRequestStatus("approved");
        UserResponse response = mapToResponse(userRepository.save(user));
        editLogRepository.save(new com.incubator.management.entity.EditLog(id, "EDIT_APPROVED", "Admin approved profile edit request"));
        activityLogRepository.save(new com.incubator.management.entity.ActivityLog(id, "EDIT_APPROVED", "Profile edit approved by admin"));

        // Notify user
        notificationService.createNotification(
                user.getId(),
                "✅ Profile Edit Approved",
                "Your profile edit request has been approved. Your changes are now visible.",
                "GENERAL",
                null
        );

        return response;
    }

    public UserResponse rejectEdit(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEditRequestStatus("none");
        UserResponse response = mapToResponse(userRepository.save(user));
        activityLogRepository.save(new com.incubator.management.entity.ActivityLog(id, "EDIT_REJECTED", "Profile edit request rejected by admin"));

        // Notify user
        notificationService.createNotification(
                user.getId(),
                "❌ Profile Edit Rejected",
                "Your profile edit request has been rejected by the admin.",
                "GENERAL",
                null
        );

        return response;
    }

    /**
     * Retrieve all users in the system.
     * Used by the Admin manage-users page.
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Retrieve all users with a given role (e.g. MENTOR, STUDENT).
     * Used to populate the mentor dropdown in the assign-mentors page.
     */
    public List<UserResponse> getUsersByRole(String role) {
        User.Role userRole = User.Role.valueOf(role.toUpperCase());
        return userRepository.findByRole(userRole).stream()
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Deactivate a user account (soft delete).
     * The account is disabled but not removed from the database.
     */
    public UserResponse deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.isSuperAdmin()) {
            throw new RuntimeException("Super admin accounts cannot be deactivated");
        }
        user.setActive(false);
        return mapToResponse(userRepository.save(user));
    }

    /**
     * Reactive a user account.
     */
    public UserResponse reactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(true);
        return mapToResponse(userRepository.save(user));
    }

    /**
     * Permanently delete a user and all related records.
     */
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.isSuperAdmin()) {
            throw new RuntimeException("Super admin accounts cannot be deleted");
        }

        // Delete all related records before removing the user (FK constraints)
        notificationRepository.deleteByUserId(userId);
        pitchDocumentRepository.deleteByUploadedByUserId(userId);
        evaluationRepository.deleteByMentorId(userId);
        mentorAssignmentRepository.deleteByMentorId(userId);
        mentorAssignmentRepository.deleteByAssignedById(userId);
        fundingRepository.deleteByInvestorId(userId);

        // Delete startups by this founder (DB cascade handles their children)
        List<Startup> startups = startupRepository.findByFounder(user);
        for (Startup s : startups) {
            startupRepository.delete(s);
        }

        // Delete the user (JPA cascade handles role profiles)
        userRepository.delete(user);
    }

    /**
     * Permanently delete a user AND block their email so it can never be re-registered.
     */
    @Transactional
    public void blockAndDeleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Block the email
        if (!blockedEmailRepository.existsByEmail(user.getEmail())) {
            blockedEmailRepository.save(new BlockedEmail(user.getEmail()));
        }

        // Delete user (same cleanup as deleteUser)
        deleteUser(userId);
    }

    /**
     * Retrieve a user by their ID.
     */
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(user);
    }

    /**
     * Block a user account without deleting it.
     * Blocked users cannot log in but their data remains in the system.
     */
    public UserResponse blockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.isSuperAdmin()) {
            throw new RuntimeException("Super admin accounts cannot be blocked");
        }
        user.setBlocked(true);
        UserResponse response = mapToResponse(userRepository.save(user));
        activityLogRepository.save(new com.incubator.management.entity.ActivityLog(id, "BLOCKED", "User blocked by admin"));
        return response;
    }

    /**
     * Unblock a user account.
     */
    public UserResponse unblockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setBlocked(false);
        UserResponse response = mapToResponse(userRepository.save(user));
        activityLogRepository.save(new com.incubator.management.entity.ActivityLog(id, "UNBLOCKED", "User unblocked by admin"));
        return response;
    }

    /**
     * Retrieve all blocked users.
     */
    public List<UserResponse> getBlockedUsers() {
        return userRepository.findByIsBlocked(true).stream()
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Map<String, Object>> getEditLogs(Long userId) {
        return editLogRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(log -> {
                    Map<String, Object> m = new java.util.HashMap<>();
                    m.put("action", log.getAction());
                    m.put("details", log.getDetails());
                    m.put("createdAt", log.getCreatedAt() != null ? log.getCreatedAt().toString() : null);
                    return m;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Map<String, Object>> getActivities(Long userId) {
        return activityLogRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(log -> {
                    Map<String, Object> m = new java.util.HashMap<>();
                    m.put("action", log.getAction());
                    m.put("details", log.getDetails());
                    m.put("createdAt", log.getCreatedAt() != null ? log.getCreatedAt().toString() : null);
                    return m;
                })
                .collect(java.util.stream.Collectors.toList());
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
        response.setPhoto(user.getPhoto());
        response.setAge(user.getAge());
        response.setSex(user.getSex());
        response.setNidNo(user.getNidNo());
        response.setAddress(user.getAddress());
        response.setDegree(user.getDegree());
        response.setEducation(user.getEducation());
        response.setBio(user.getBio());
        response.setExpertise(user.getExpertise());
        response.setNidCertificate(user.getNidCertificate());
        response.setAcademicCertificate(user.getAcademicCertificate());
        response.setEditRequestStatus(user.getEditRequestStatus());
        response.setActive(user.isActive());
        response.setBlocked(user.isBlocked());
        response.setSuperAdmin(user.isSuperAdmin());

        if (user.getRole() == User.Role.STUDENT && user.getStudentProfile() != null) {
            response.setUniversity(user.getStudentProfile().getUniversity());
            response.setDepartment(user.getStudentProfile().getDepartment());
            response.setBatchYear(user.getStudentProfile().getBatchYear());
        }

        if (user.getRole() == User.Role.MENTOR && user.getMentorProfile() != null) {
            response.setExpertiseArea(user.getMentorProfile().getExpertiseArea());
            response.setOrganization(user.getMentorProfile().getOrganization());
            response.setYearsExperience(user.getMentorProfile().getYearsExperience());
        }

        if (user.getRole() == User.Role.INVESTOR && user.getInvestorProfile() != null) {
            response.setInvestmentFocus(user.getInvestorProfile().getInvestmentFocus());
        }

        return response;
    }
}
