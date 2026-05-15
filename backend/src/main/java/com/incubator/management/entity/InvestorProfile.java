package com.incubator.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
    private User user;

    @Column(name = "firm_name")
    private String firmName;

    @Column(name = "investment_focus")
    private String investmentFocus;

    @Column(name = "min_investment")
    private BigDecimal minInvestment;

    @Column(name = "max_investment")
    private BigDecimal maxInvestment;
}
