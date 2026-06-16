package com.incubator.management.dto;

import java.math.BigDecimal;

/**
 * Startup Request DTO
 * -------------------
 * Data Transfer Object used to receive startup creation data from the client.
 * Maps to the JSON body of POST /api/startups.
 */
public class StartupRequest {

    private String name;         // Startup name (e.g. "EcoPack Solutions")
    private String description;  // What the startup does
    private String industry;     // Industry sector (e.g. "HealthTech", "FinTech")
    private String stage;        // Current stage (e.g. "Idea", "MVP", "Growth")
    private Long founderId;      // ID of the User who is creating this startup
    private Integer progress;
    private BigDecimal fundingGoal;
    private BigDecimal currentFunding;

    // ─── Constructors ─────────────────────────────────────────────────────────

    public StartupRequest() {}

    public StartupRequest(String name, String description, String industry,
                          String stage, Long founderId) {
        this.name = name;
        this.description = description;
        this.industry = industry;
        this.stage = stage;
        this.founderId = founderId;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getIndustry() {
        return industry;
    }

    public String getStage() {
        return stage;
    }

    public Long getFounderId() {
        return founderId;
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

    // ─── Setters ──────────────────────────────────────────────────────────────

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public void setFounderId(Long founderId) {
        this.founderId = founderId;
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
}
