package com.incubator.management.dto;

import java.time.LocalDateTime;

/**
 * Startup Response DTO
 * --------------------
 * Safe response object returned to the client after startup operations.
 * Contains only the data the frontend needs — no internal entity references.
 * Built by StartupService.mapToResponse().
 */
public class StartupResponse {

    private Long id;                   // Unique startup ID
    private String name;               // Startup name
    private String description;        // Startup description
    private String founderName;        // Display name of the founder (not their ID)
    private String industry;           // Industry sector
    private String stage;              // Development stage
    private String status;             // Approval status: "pending", "approved", "rejected"
    private LocalDateTime submittedAt; // When the startup was first submitted

    // ─── Constructors ─────────────────────────────────────────────────────────

    public StartupResponse() {}

    public StartupResponse(Long id, String name, String description, String founderName,
                           String industry, String stage, String status,
                           LocalDateTime submittedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.founderName = founderName;
        this.industry = industry;
        this.stage = stage;
        this.status = status;
        this.submittedAt = submittedAt;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getFounderName() {
        return founderName;
    }

    public String getIndustry() {
        return industry;
    }

    public String getStage() {
        return stage;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    // ─── Setters ──────────────────────────────────────────────────────────────

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFounderName(String founderName) {
        this.founderName = founderName;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}
