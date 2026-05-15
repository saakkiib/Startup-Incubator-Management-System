package com.incubator.management.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Startup Response DTO
 * --------------------
 * Safe response object returned to the client after startup operations.
 * Contains only the data the frontend needs — no internal entity references.
 * Built by StartupService.mapToResponse().
 */
@Data
@Builder
public class StartupResponse {
    private Long id;                   // Unique startup ID
    private String name;               // Startup name
    private String description;        // Startup description
    private String founderName;        // Display name of the founder (not their ID)
    private String industry;           // Industry sector
    private String stage;              // Development stage
    private String status;             // Approval status: "pending", "approved", "rejected"
    private LocalDateTime submittedAt; // When the startup was first submitted
}
