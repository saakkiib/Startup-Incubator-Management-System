package com.incubator.management.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Funding Response DTO
 * --------------------
 * Flat response object returned to the client for investment records.
 * Avoids circular JSON serialization from the Funding entity's
 * @ManyToOne relationships to Startup and User.
 * Used by: investor-dashboard.js, investor-history.js, investor-profile.js
 */
public class FundingResponseDto {

    private Long id;
    private Long startupId;
    private String startupName;
    private Long investorId;
    private String investorName;
    private BigDecimal amount;
    private BigDecimal equityPercentage;
    private String fundingType;
    private String terms;
    private String status;
    private LocalDateTime investedAt;

    // ─── Constructors ─────────────────────────────────────────────────────────

    public FundingResponseDto() {}

    // ─── Getters ──────────────────────────────────────────────────────────────

    public Long getId()                     { return id; }
    public Long getStartupId()              { return startupId; }
    public String getStartupName()          { return startupName; }
    public Long getInvestorId()             { return investorId; }
    public String getInvestorName()         { return investorName; }
    public BigDecimal getAmount()           { return amount; }
    public BigDecimal getEquityPercentage() { return equityPercentage; }
    public BigDecimal getEquity()           { return equityPercentage; }
    public String getFundingType()          { return fundingType; }
    public String getTerms()                { return terms; }
    public String getStatus()               { return status; }
    public LocalDateTime getInvestedAt()    { return investedAt; }

    // ─── Setters ──────────────────────────────────────────────────────────────

    public void setId(Long id)                                   { this.id = id; }
    public void setStartupId(Long startupId)                     { this.startupId = startupId; }
    public void setStartupName(String startupName)               { this.startupName = startupName; }
    public void setInvestorId(Long investorId)                   { this.investorId = investorId; }
    public void setInvestorName(String investorName)             { this.investorName = investorName; }
    public void setAmount(BigDecimal amount)                     { this.amount = amount; }
    public void setEquityPercentage(BigDecimal equityPercentage) { this.equityPercentage = equityPercentage; }
    public void setEquity(BigDecimal equity)                     { this.equityPercentage = equity; }
    public void setFundingType(String fundingType)               { this.fundingType = fundingType; }
    public void setTerms(String terms)                           { this.terms = terms; }
    public void setStatus(String status)                         { this.status = status; }
    public void setInvestedAt(LocalDateTime investedAt)          { this.investedAt = investedAt; }
}
