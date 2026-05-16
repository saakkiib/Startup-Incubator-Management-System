package com.incubator.management.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Funding Entity
 * --------------
 * Maps to the "funding" table.
 * Records a completed investment transaction from an investor to a startup,
 * including the amount, equity percentage, and deal terms.
 */
@Entity
@Table(name = "funding")
public class Funding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "funding_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "startup_id", nullable = false)
    private Startup startup;                // The startup receiving the investment

    @ManyToOne
    @JoinColumn(name = "investor_id", nullable = false)
    private User investor;                  // The investor providing the funds

    @Column(nullable = false)
    private BigDecimal amount;              // Total investment amount in USD

    @Column(name = "equity_percentage")
    private BigDecimal equityPercentage;    // Percentage of equity given in return (e.g. 5.00 = 5%)

    @Column(name = "funding_type")
    private String fundingType;             // Round type: "Seed", "Series A", "Angel", etc.

    @Column(columnDefinition = "TEXT")
    private String terms;                   // Legal/contractual terms of the deal

    private String status = "pending";      // Deal status: "pending", "completed", or "cancelled"

    @CreationTimestamp
    @Column(name = "funded_at", updatable = false)
    private LocalDateTime fundedAt;         // Timestamp when the funding record was created

    // ─── Constructors ─────────────────────────────────────────────────────────

    // Default (no-arg) constructor — required by JPA
    public Funding() {}

    // All-args constructor for convenience
    public Funding(Long id, Startup startup, User investor, BigDecimal amount,
                   BigDecimal equityPercentage, String fundingType, String terms,
                   String status, LocalDateTime fundedAt) {
        this.id = id;
        this.startup = startup;
        this.investor = investor;
        this.amount = amount;
        this.equityPercentage = equityPercentage;
        this.fundingType = fundingType;
        this.terms = terms;
        this.status = status;
        this.fundedAt = fundedAt;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public Startup getStartup() {
        return startup;
    }

    public User getInvestor() {
        return investor;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getEquityPercentage() {
        return equityPercentage;
    }

    public String getFundingType() {
        return fundingType;
    }

    public String getTerms() {
        return terms;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getFundedAt() {
        return fundedAt;
    }

    // ─── Setters ──────────────────────────────────────────────────────────────

    public void setId(Long id) {
        this.id = id;
    }

    public void setStartup(Startup startup) {
        this.startup = startup;
    }

    public void setInvestor(User investor) {
        this.investor = investor;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setEquityPercentage(BigDecimal equityPercentage) {
        this.equityPercentage = equityPercentage;
    }

    public void setFundingType(String fundingType) {
        this.fundingType = fundingType;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFundedAt(LocalDateTime fundedAt) {
        this.fundedAt = fundedAt;
    }

    // ─── toString ─────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "Funding{" +
                "id=" + id +
                ", amount=" + amount +
                ", fundingType='" + fundingType + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
