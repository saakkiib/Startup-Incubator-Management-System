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
 *   POST /api/mentor-assignments                    → Assign a mentor to a startup
 *   GET  /api/mentor-assignments                    → Get all assignments (Admin overview)
 *   GET  /api/mentor-assignments/startup/{id}       → Get all mentors assigned to a startup
 *   GET  /api/mentor-assignments/mentor/{mentorId}  → Get all startups assigned to a mentor
 */
@RestController
@RequestMapping("/api/mentor-assignments")
public class MentorAssignmentController {

    private final MentorAssignmentService assignmentService;

    public MentorAssignmentController(MentorAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    /**
     * Assign a mentor to a startup.
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
     * Get all mentor assignments in the system (Admin overview).
     */
    @GetMapping
    public ResponseEntity<List<MentorAssignment>> getAllAssignments() {
        return ResponseEntity.ok(assignmentService.getAllAssignments());
    }

    /**
     * Get all mentor assignments for a specific startup.
     */
    @GetMapping("/startup/{id}")
    public ResponseEntity<List<MentorAssignment>> getByStartup(@PathVariable Long id) {
        return ResponseEntity.ok(assignmentService.getAssignmentsByStartup(id));
    }

    /**
     * Get all startup assignments for a specific mentor.
     * Used on the mentor dashboard to show their workload.
     */
    @GetMapping("/mentor/{mentorId}")
    public ResponseEntity<List<MentorAssignment>> getByMentor(@PathVariable Long mentorId) {
        return ResponseEntity.ok(assignmentService.getAssignmentsByMentor(mentorId));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Inner Request DTO
    // ─────────────────────────────────────────────────────────────────────────
    public static class AssignmentRequest {
        private Long startupId;
        private Long mentorId;
        private Long adminId;
        private String notes;

        public Long getStartupId()  { return startupId; }
        public Long getMentorId()   { return mentorId; }
        public Long getAdminId()    { return adminId; }
        public String getNotes()    { return notes; }

        public void setStartupId(Long startupId) { this.startupId = startupId; }
        public void setMentorId(Long mentorId)   { this.mentorId = mentorId; }
        public void setAdminId(Long adminId)     { this.adminId = adminId; }
        public void setNotes(String notes)       { this.notes = notes; }
    }
}
