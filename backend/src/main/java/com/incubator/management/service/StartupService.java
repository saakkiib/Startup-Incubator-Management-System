package com.incubator.management.service;

import com.incubator.management.dto.StartupRequest;
import com.incubator.management.dto.StartupResponse;
import com.incubator.management.entity.Startup;
import com.incubator.management.entity.User;
import com.incubator.management.repository.StartupRepository;
import com.incubator.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Startup Service
 * ---------------
 * Business logic for all startup-related operations:
 * - Creating new startups
 * - Fetching all startups
 * - Approving a startup
 * - Mapping Startup entities to StartupResponse DTOs
 */
@Service
@RequiredArgsConstructor
public class StartupService {

    private final StartupRepository startupRepository;
    private final UserRepository userRepository;

    /**
     * Create and save a new startup.
     * Looks up the founder by ID to establish the relationship.
     * New startups always start with "pending" status.
     */
    public StartupResponse createStartup(StartupRequest request) {
        // Verify the founder exists in the system
        User founder = userRepository.findById(request.getFounderId())
                .orElseThrow(() -> new RuntimeException("Founder not found"));

        Startup startup = Startup.builder()
                .name(request.getName())
                .description(request.getDescription())
                .founder(founder)
                .industry(request.getIndustry())
                .stage(request.getStage())
                .status("pending") // All startups start as pending until an admin approves
                .build();

        Startup savedStartup = startupRepository.save(startup);
        return mapToResponse(savedStartup);
    }

    /**
     * Fetch all startups currently in the system.
     * Maps each Startup entity to a StartupResponse DTO.
     */
    public List<StartupResponse> getAllStartups() {
        return startupRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Approve a startup by changing its status to "approved".
     * Typically triggered by an Admin or Mentor.
     */
    public StartupResponse approveStartup(Long id) {
        Startup startup = startupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Startup not found"));
        startup.setStatus("approved");
        return mapToResponse(startupRepository.save(startup));
    }

    /**
     * Convert a Startup entity to a StartupResponse DTO for API responses.
     */
    public StartupResponse mapToResponse(Startup startup) {
        return StartupResponse.builder()
                .id(startup.getId())
                .name(startup.getName())
                .description(startup.getDescription())
                .founderName(startup.getFounder().getName())
                .industry(startup.getIndustry())
                .stage(startup.getStage())
                .status(startup.getStatus())
                .submittedAt(startup.getSubmittedAt())
                .build();
    }
}
