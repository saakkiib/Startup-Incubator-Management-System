package com.incubator.management.service;

import com.incubator.management.entity.MentorAssignment;
import com.incubator.management.entity.Startup;
import com.incubator.management.entity.User;
import com.incubator.management.repository.MentorAssignmentRepository;
import com.incubator.management.repository.StartupRepository;
import com.incubator.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorAssignmentService {

    private final MentorAssignmentRepository assignmentRepository;
    private final StartupRepository startupRepository;
    private final UserRepository userRepository;

    public MentorAssignment assignMentor(Long startupId, Long mentorId, Long adminId, String notes) {
        Startup startup = startupRepository.findById(startupId)
                .orElseThrow(() -> new RuntimeException("Startup not found"));
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new RuntimeException("Mentor not found"));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        MentorAssignment assignment = MentorAssignment.builder()
                .startup(startup)
                .mentor(mentor)
                .assignedBy(admin)
                .notes(notes)
                .status("active")
                .build();

        return assignmentRepository.save(assignment);
    }

    public List<MentorAssignment> getAssignmentsByStartup(Long startupId) {
        Startup startup = startupRepository.findById(startupId)
                .orElseThrow(() -> new RuntimeException("Startup not found"));
        return assignmentRepository.findByStartup(startup);
    }
}
