package com.incubator.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
