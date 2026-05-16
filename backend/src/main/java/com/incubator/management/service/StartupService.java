package com.incubator.management.service;

import com.incubator.management.dto.StartupRequest;
import com.incubator.management.dto.StartupResponse;
import com.incubator.management.entity.Startup;
import com.incubator.management.entity.User;
import com.incubator.management.repository.StartupRepository;
import com.incubator.management.repository.UserRepository;
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
public class StartupService {

    private final StartupRepository startupRepository;
    private final UserRepository userRepository;

    // Constructor injection — replaces Lombok @RequiredArgsConstructor
    public StartupService(StartupRepository startupRepository,
                          UserRepository userRepository) {
        this.startupRepository = startupRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create and save a new startup.
     * Looks up the founder by ID to establish the relationship.
     * New startups always start with "pending" status.
     */
    public StartupResponse createStartup(StartupRequest request) {
        // Verify the founder exists in the system
        User founder = userRepository.findById(request.getFounderId())
                .orElseThrow(() -> new RuntimeException("Founder not found"));

        // Build Startup object using setters instead of @Builder
        Startup startup = new Startup();
        startup.setName(request.getName());
        startup.setDescription(request.getDescription());
        startup.setFounder(founder);
        startup.setIndustry(request.getIndustry());
        startup.setStage(request.getStage());
        startup.setStatus("pending"); // All startups start as pending until an admin approves

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
     * Uses setters instead of @Builder.
     */
    public StartupResponse mapToResponse(Startup startup) {
        StartupResponse response = new StartupResponse();
        response.setId(startup.getId());
        response.setName(startup.getName());
        response.setDescription(startup.getDescription());
        response.setFounderName(startup.getFounder().getFullName()); // Using getter
        response.setIndustry(startup.getIndustry());
        response.setStage(startup.getStage());
        response.setStatus(startup.getStatus());
        response.setSubmittedAt(startup.getSubmittedAt());
        return response;
    }
}
