package com.incubator.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

/**
 * InvestorProfile Entity
 * ----------------------
 * Maps to the "investor_profiles" table.
 * Extends the base User with investor-specific details like firm info
 * and investment range preferences.
 * Created automatically (blank) when a user registers as INVESTOR.
 */
@Entity
@Table(name = "investor_profiles")
public class InvestorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;                   // The investor user this profile belongs to

    @Column(name = "firm_name")
    private String firmName;              // Venture capital or angel firm name (e.g. "Sequoia Capital")

    @Column(name = "investment_focus")
    private String investmentFocus;       // Preferred sectors (e.g. "HealthTech, EdTech")

    @Min(100000)
    @Max(100000000)
    @Column(name = "min_investment")
    private BigDecimal minInvestment;     // Minimum deal size the investor will consider (in USD)

    @Column(name = "max_investment")
    private BigDecimal maxInvestment;     // Maximum deal size the investor will consider (in USD)

    // ─── Constructors ─────────────────────────────────────────────────────────

    // Default (no-arg) constructor — required by JPA
    public InvestorProfile() {}

    // All-args constructor for convenience
    public InvestorProfile(Long id, User user, String firmName, String investmentFocus,
                           BigDecimal minInvestment, BigDecimal maxInvestment) {
        this.id = id;
        this.user = user;
        this.firmName = firmName;
        this.investmentFocus = investmentFocus;
        this.minInvestment = minInvestment;
        this.maxInvestment = maxInvestment;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getFirmName() {
        return firmName;
    }

    public String getInvestmentFocus() {
        return investmentFocus;
    }

    public BigDecimal getMinInvestment() {
        return minInvestment;
    }

    public BigDecimal getMaxInvestment() {
        return maxInvestment;
    }

    // ─── Setters ──────────────────────────────────────────────────────────────

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    public void setInvestmentFocus(String investmentFocus) {
        this.investmentFocus = investmentFocus;
    }

    public void setMinInvestment(BigDecimal minInvestment) {
        this.minInvestment = minInvestment;
    }

    public void setMaxInvestment(BigDecimal maxInvestment) {
        this.maxInvestment = maxInvestment;
    }

    // ─── toString ─────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "InvestorProfile{" +
                "id=" + id +
                ", firmName='" + firmName + '\'' +
                ", investmentFocus='" + investmentFocus + '\'' +
                ", minInvestment=" + minInvestment +
                ", maxInvestment=" + maxInvestment +
                '}';
    }
}
