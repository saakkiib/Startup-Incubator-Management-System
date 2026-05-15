package com.incubator.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Evaluation Entity
 * -----------------
 * Maps to the "evaluations" table.
 * Stores a mentor's scored evaluation of a startup across three dimensions:
 * Technical, Market, and Financial.
 */
@Entity
@Table(name = "evaluations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluation_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "startup_id", nullable = false)
    private Startup startup;              // The startup being evaluated

    @ManyToOne
    @JoinColumn(name = "mentor_id", nullable = false)
    private User mentor;                  // The mentor who submitted this evaluation

    // ─── Scores (typically out of 100) ────────────────────────────────────────

    @Column(name = "technical_score")
    private Integer technicalScore;       // How technically sound is the product?

    @Column(name = "market_score")
    private Integer marketScore;          // How big and accessible is the target market?

    @Column(name = "financial_score")
    private Integer financialScore;       // How strong is the financial model?

    @Column(name = "overall_score")
    private Integer overallScore;         // Aggregate score across all dimensions

    // ─── Feedback (Free-text) ─────────────────────────────────────────────────

    @Column(name = "technical_feedback", columnDefinition = "TEXT")
    private String technicalFeedback;     // Detailed technical strengths/weaknesses

    @Column(name = "market_feedback", columnDefinition = "TEXT")
    private String marketFeedback;        // Market opportunity analysis notes

    @Column(name = "financial_feedback", columnDefinition = "TEXT")
    private String financialFeedback;     // Financial viability comments

    @Column(columnDefinition = "TEXT")
    private String recommendation;        // Final recommendation (e.g. "Fund", "Needs work", "Reject")

    @CreationTimestamp
    @Column(name = "evaluated_at", updatable = false)
    private LocalDateTime evaluatedAt;    // Timestamp when the evaluation was submitted
}
