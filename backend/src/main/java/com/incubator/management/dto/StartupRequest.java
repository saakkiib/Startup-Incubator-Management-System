package com.incubator.management.dto;

import lombok.Data;

/**
 * Startup Request DTO
 * -------------------
 * Data Transfer Object used to receive startup creation data from the client.
 * Maps to the JSON body of POST /api/startups.
 */
@Data
public class StartupRequest {
    private String name;         // Startup name (e.g. "EcoPack Solutions")
    private String description;  // What the startup does
    private String industry;     // Industry sector (e.g. "HealthTech", "FinTech")
    private String stage;        // Current stage (e.g. "Idea", "MVP", "Growth")
    private Long founderId;      // ID of the User who is creating this startup
}
