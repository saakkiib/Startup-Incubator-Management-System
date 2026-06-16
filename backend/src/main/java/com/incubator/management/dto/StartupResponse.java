package com.incubator.management.dto;

import java.math.BigDecimal;
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
    private Long founderId;            // ID of the founder user
    private String founderName;        // Display name of the founder (not their ID)
    private String industry;           // Industry sector
    private String stage;              // Development stage
    private String status;             // Approval status: "pending", "approved", "rejected"
    private Integer progress;
    private BigDecimal fundingGoal;
    private BigDecimal currentFunding;
    private String rejectionReason;
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

    public Long getFounderId() {
        return founderId;
    }

    public String getFounderName() {
        return founderName;
    }

    // Alias: some JS files access s.founder instead of s.founderName
    public String getFounder() {
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

    public Integer getProgress() {
        return progress;
    }

    public BigDecimal getFundingGoal() {
        return fundingGoal;
    }

    public BigDecimal getCurrentFunding() {
        return currentFunding;
    }

    public String getRejectionReason() {
        return rejectionReason;
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

    public void setFounderId(Long founderId) {
        this.founderId = founderId;
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

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public void setFundingGoal(BigDecimal fundingGoal) {
        this.fundingGoal = fundingGoal;
    }

    public void setCurrentFunding(BigDecimal currentFunding) {
        this.currentFunding = currentFunding;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}
