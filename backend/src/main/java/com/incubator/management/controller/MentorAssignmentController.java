package com.incubator.management.controller;

import com.incubator.management.entity.MentorAssignment;
import com.incubator.management.service.MentorAssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Mentor Assignment Controller
 * ----------------------------
 * Handles assigning mentors to startups (Admin action).
 * Base URL: /api/mentor-assignments
 *
 * Endpoints:
 *   POST /api/mentor-assignments              → Assign a mentor to a startup
 *   GET  /api/mentor-assignments/startup/{id} → Get all mentors assigned to a startup
 */
@RestController
@RequestMapping("/api/mentor-assignments")
public class MentorAssignmentController {

    private final MentorAssignmentService assignmentService;

    // Constructor injection — replaces Lombok @RequiredArgsConstructor
    public MentorAssignmentController(MentorAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    /**
     * Assign a mentor to a startup.
     * The admin initiating the assignment must also be identified.
     */
    @PostMapping
    public ResponseEntity<MentorAssignment> assignMentor(@RequestBody AssignmentRequest request) {
        return ResponseEntity.ok(assignmentService.assignMentor(
                request.getStartupId(),
                request.getMentorId(),
                request.getAdminId(),
                request.getNotes()
        ));
    }

    /**
     * Get all mentor assignments for a specific startup.
     */
    @GetMapping("/startup/{id}")
    public ResponseEntity<List<MentorAssignment>> getByStartup(@PathVariable Long id) {
        return ResponseEntity.ok(assignmentService.getAssignmentsByStartup(id));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Inner Request DTO — with manual getters and setters
    // Used to receive assignment data from the request body.
    // ─────────────────────────────────────────────────────────────────────────
    public static class AssignmentRequest {

        private Long startupId; // The startup receiving the mentor
        private Long mentorId;  // The mentor being assigned
        private Long adminId;   // The admin performing the assignment
        private String notes;   // Optional notes about the assignment

        // Getters
        public Long getStartupId()  { return startupId; }
        public Long getMentorId()   { return mentorId; }
        public Long getAdminId()    { return adminId; }
        public String getNotes()    { return notes; }

        // Setters
        public void setStartupId(Long startupId) { this.startupId = startupId; }
        public void setMentorId(Long mentorId)   { this.mentorId = mentorId; }
        public void setAdminId(Long adminId)     { this.adminId = adminId; }
        public void setNotes(String notes)       { this.notes = notes; }
    }
}
