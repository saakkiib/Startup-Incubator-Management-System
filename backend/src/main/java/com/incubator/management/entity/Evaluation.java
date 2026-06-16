package com.incubator.management.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
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
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluation_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "startup_id", nullable = false)
    @JsonIgnoreProperties({"founder", "studentProfile", "mentorProfile", "investorProfile", "password"})
    private Startup startup;              // The startup being evaluated

    @ManyToOne
    @JoinColumn(name = "mentor_id", nullable = false)
    @JsonIgnoreProperties({"studentProfile", "mentorProfile", "investorProfile", "password"})
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

    // ─── Constructors ─────────────────────────────────────────────────────────

    // Default (no-arg) constructor — required by JPA
    public Evaluation() {}

    // All-args constructor for convenience
    public Evaluation(Long id, Startup startup, User mentor,
                      Integer technicalScore, Integer marketScore, Integer financialScore,
                      Integer overallScore, String technicalFeedback, String marketFeedback,
                      String financialFeedback, String recommendation, LocalDateTime evaluatedAt) {
        this.id = id;
        this.startup = startup;
        this.mentor = mentor;
        this.technicalScore = technicalScore;
        this.marketScore = marketScore;
        this.financialScore = financialScore;
        this.overallScore = overallScore;
        this.technicalFeedback = technicalFeedback;
        this.marketFeedback = marketFeedback;
        this.financialFeedback = financialFeedback;
        this.recommendation = recommendation;
        this.evaluatedAt = evaluatedAt;
    }



    public Long getId() {
        return id;
    }

    public Startup getStartup() {
        return startup;
    }

    public User getMentor() {
        return mentor;
    }

    public Integer getTechnicalScore() {
        return technicalScore;
    }

    public Integer getMarketScore() {
        return marketScore;
    }

    public Integer getFinancialScore() {
        return financialScore;
    }

    public Integer getOverallScore() {
        return overallScore;
    }

    public String getTechnicalFeedback() {
        return technicalFeedback;
    }

    public String getMarketFeedback() {
        return marketFeedback;
    }

    public String getFinancialFeedback() {
        return financialFeedback;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public LocalDateTime getEvaluatedAt() {
        return evaluatedAt;
    }

    // ─── Setters ──────────────────────────────────────────────────────────────

    public void setId(Long id) {
        this.id = id;
    }

    public void setStartup(Startup startup) {
        this.startup = startup;
    }

    public void setMentor(User mentor) {
        this.mentor = mentor;
    }

    public void setTechnicalScore(Integer technicalScore) {
        this.technicalScore = technicalScore;
    }

    public void setMarketScore(Integer marketScore) {
        this.marketScore = marketScore;
    }

    public void setFinancialScore(Integer financialScore) {
        this.financialScore = financialScore;
    }

    public void setOverallScore(Integer overallScore) {
        this.overallScore = overallScore;
    }

    public void setTechnicalFeedback(String technicalFeedback) {
        this.technicalFeedback = technicalFeedback;
    }

    public void setMarketFeedback(String marketFeedback) {
        this.marketFeedback = marketFeedback;
    }

    public void setFinancialFeedback(String financialFeedback) {
        this.financialFeedback = financialFeedback;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public void setEvaluatedAt(LocalDateTime evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }

    // ─── toString ─────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "Evaluation{" +
                "id=" + id +
                ", overallScore=" + overallScore +
                ", recommendation='" + recommendation + '\'' +
                '}';
    }
}
