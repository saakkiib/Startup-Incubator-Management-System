package com.incubator.management.controller;

import com.incubator.management.entity.MentorAssignment;
import com.incubator.management.service.MentorAssignmentService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mentor-assignments")
@RequiredArgsConstructor
public class MentorAssignmentController {

    private final MentorAssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<MentorAssignment> assignMentor(@RequestBody AssignmentRequest request) {
        return ResponseEntity.ok(assignmentService.assignMentor(
                request.getStartupId(),
                request.getMentorId(),
                request.getAdminId(),
                request.getNotes()
        ));
    }

    @GetMapping("/startup/{id}")
    public ResponseEntity<List<MentorAssignment>> getByStartup(@PathVariable Long id) {
        return ResponseEntity.ok(assignmentService.getAssignmentsByStartup(id));
    }

    @Data
    public static class AssignmentRequest {
        private Long startupId;
        private Long mentorId;
        private Long adminId;
        private String notes;
    }
}
