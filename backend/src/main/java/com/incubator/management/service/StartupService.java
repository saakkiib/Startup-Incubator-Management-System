package com.incubator.management.service;

import com.incubator.management.dto.StartupRequest;
import com.incubator.management.dto.StartupResponse;
import com.incubator.management.entity.ActivityLog;
import com.incubator.management.entity.Startup;
import com.incubator.management.entity.User;
import com.incubator.management.repository.ActivityLogRepository;
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
    private final NotificationService notificationService;
    private final ActivityLogRepository activityLogRepository;

    // Constructor injection
    public StartupService(StartupRepository startupRepository,
                          UserRepository userRepository,
                          NotificationService notificationService,
                          ActivityLogRepository activityLogRepository) {
        this.startupRepository = startupRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.activityLogRepository = activityLogRepository;
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
        startup.setProgress(request.getProgress() != null ? request.getProgress() : 0);
        startup.setFundingGoal(request.getFundingGoal() != null ? request.getFundingGoal() : java.math.BigDecimal.ZERO);
        startup.setCurrentFunding(request.getCurrentFunding() != null ? request.getCurrentFunding() : java.math.BigDecimal.ZERO);

        Startup savedStartup = startupRepository.save(startup);
        activityLogRepository.save(new ActivityLog(request.getFounderId(), "STARTUP_CREATED", "Created startup \"" + startup.getName() + "\" in " + startup.getIndustry() + " industry"));
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
        StartupResponse response = mapToResponse(startupRepository.save(startup));

        // Notify founder
        User founder = startup.getFounder();
        notificationService.createNotification(
                founder.getId(),
                "✅ Startup Approved!",
                "Your startup \"" + startup.getName() + "\" has been approved by the admin.",
                "STARTUP_APPROVED",
                startup.getId()
        );

        activityLogRepository.save(new ActivityLog(founder.getId(), "STARTUP_APPROVED", "Your startup \"" + startup.getName() + "\" has been approved"));

        return response;
    }

    /**
     * Reject a startup by changing its status to "rejected".
     * Typically triggered by an Admin when reviewing a startup submission.
     */
    public StartupResponse rejectStartup(Long id, String reason) {
        Startup startup = startupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Startup not found"));
        startup.setStatus("rejected");
        startup.setRejectionReason(reason);
        StartupResponse response = mapToResponse(startupRepository.save(startup));

        // Notify founder
        User founder = startup.getFounder();
        String msg = "Your startup \"" + startup.getName() + "\" has been rejected.";
        if (reason != null && !reason.isEmpty()) {
            msg += " Reason: " + reason;
        }
        notificationService.createNotification(
                founder.getId(),
                "❌ Startup Rejected",
                msg,
                "STARTUP_REJECTED",
                startup.getId()
        );

        activityLogRepository.save(new ActivityLog(founder.getId(), "STARTUP_REJECTED", "Your startup \"" + startup.getName() + "\" has been rejected" + (reason != null && !reason.isEmpty() ? ". Reason: " + reason : "")));

        return response;
    }

    /**
     * Retrieve a single startup by its ID.
     */
    public StartupResponse getStartupById(Long id) {
        Startup startup = startupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Startup not found"));
        return mapToResponse(startup);
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
        response.setFounderId(startup.getFounder().getId());           // ID of the founder
        response.setFounderName(startup.getFounder().getFullName());   // Display name
        response.setIndustry(startup.getIndustry());
        response.setStage(startup.getStage());
        response.setStatus(startup.getStatus());
        response.setProgress(startup.getProgress());
        response.setFundingGoal(startup.getFundingGoal());
        response.setCurrentFunding(startup.getCurrentFunding());
        response.setRejectionReason(startup.getRejectionReason());
        response.setSubmittedAt(startup.getSubmittedAt());
        return response;
    }

    /**
     * Retrieve all startups created by a specific founder.
     */
    public List<StartupResponse> getStartupsByFounder(Long founderId) {
        User founder = userRepository.findById(founderId)
                .orElseThrow(() -> new RuntimeException("Founder not found"));
        return startupRepository.findByFounder(founder).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update startup progress.
     */
    public StartupResponse updateProgress(Long id, Integer progress) {
        Startup startup = startupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Startup not found"));
        startup.setProgress(progress);
        return mapToResponse(startupRepository.save(startup));
    }

    /**
     * Delete a startup by its ID.
     */
    public void deleteStartup(Long id) {
        if (!startupRepository.existsById(id)) {
            throw new RuntimeException("Startup not found");
        }
        startupRepository.deleteById(id);
    }
}
