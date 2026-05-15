package com.incubator.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private Startup startup;

    @ManyToOne
    @JoinColumn(name = "investor_id", nullable = false)
    private User investor;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "equity_percentage")
    private BigDecimal equityPercentage;

    @Column(name = "funding_type")
    private String fundingType;

    @Column(columnDefinition = "TEXT")
    private String terms;

    private String status = "pending";

    @CreationTimestamp
    @Column(name = "funded_at", updatable = false)
    private LocalDateTime fundedAt;
}
