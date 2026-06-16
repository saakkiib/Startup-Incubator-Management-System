package com.incubator.management.service;

import com.incubator.management.entity.MentorAssignment;
import com.incubator.management.entity.Startup;
import com.incubator.management.entity.User;
import com.incubator.management.repository.MentorAssignmentRepository;
import com.incubator.management.repository.StartupRepository;
import com.incubator.management.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Mentor Assignment Service
 * -------------------------
 * Handles the business logic for assigning mentors to startups:
 * - Creating a new mentor assignment (Admin action)
 * - Fetching all assignments for a startup
 */
@Service
public class MentorAssignmentService {

    private final MentorAssignmentRepository assignmentRepository;
    private final StartupRepository startupRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // Constructor injection — replaces Lombok @RequiredArgsConstructor
    public MentorAssignmentService(MentorAssignmentRepository assignmentRepository,
                                   StartupRepository startupRepository,
                                   UserRepository userRepository,
                                   NotificationService notificationService) {
        this.assignmentRepository = assignmentRepository;
        this.startupRepository = startupRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    /**
     * Assign a mentor to a startup.
     * Validates that all three IDs (startup, mentor, admin) exist before saving.
     * The assignment is set to "active" status by default.
     *
     * @param startupId The startup being assigned a mentor
     * @param mentorId  The mentor being assigned
     * @param adminId   The admin performing the assignment
     * @param notes     Optional notes about the assignment
     */
    public MentorAssignment assignMentor(Long startupId, Long mentorId,
                                         Long adminId, String notes) {
        Startup startup = startupRepository.findById(startupId)
                .orElseThrow(() -> new RuntimeException("Startup not found"));
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new RuntimeException("Mentor not found"));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        // Build MentorAssignment using setters instead of @Builder
        MentorAssignment assignment = new MentorAssignment();
        assignment.setStartup(startup);
        assignment.setMentor(mentor);
        assignment.setAssignedBy(admin);
        assignment.setNotes(notes);
        assignment.setStatus("active"); // Default status when first assigned

        MentorAssignment saved = assignmentRepository.save(assignment);

        // Notify startup founder
        User founder = startup.getFounder();
        if (founder != null) {
            notificationService.createNotification(
                    founder.getId(),
                    "🎓 Mentor Assigned!",
                    mentor.getFullName() + " has been assigned as a mentor for your startup \"" + startup.getName() + "\".",
                    "MENTOR_ASSIGNED",
                    startup.getId()
            );
        }

        // Notify the mentor
        notificationService.createNotification(
                mentor.getId(),
                "📋 New Startup Assignment",
                "A new startup \"" + startup.getName() + "\" has been assigned to you.",
                "STARTUP_ASSIGNMENT",
                startup.getId()
        );

        return saved;
    }

    /**
     * Get all active and past mentor assignments for a given startup.
     */
    public List<MentorAssignment> getAssignmentsByStartup(Long startupId) {
        Startup startup = startupRepository.findById(startupId)
                .orElseThrow(() -> new RuntimeException("Startup not found"));
        return assignmentRepository.findByStartup(startup);
    }

    /**
     * Get all mentor assignments in the system.
     * Used by the Admin view for full overview.
     */
    public List<MentorAssignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    /**
     * Get all assignments where a specific mentor is involved.
     * Used on the mentor dashboard to show their assigned startups.
     */
    public List<MentorAssignment> getAssignmentsByMentor(Long mentorId) {
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new RuntimeException("Mentor not found"));
        return assignmentRepository.findByMentor(mentor);
    }
}
