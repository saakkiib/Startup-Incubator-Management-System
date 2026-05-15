package com.incubator.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Column(name = "min_investment")
    private BigDecimal minInvestment;     // Minimum deal size the investor will consider (in USD)

    @Column(name = "max_investment")
    private BigDecimal maxInvestment;     // Maximum deal size the investor will consider (in USD)
}
