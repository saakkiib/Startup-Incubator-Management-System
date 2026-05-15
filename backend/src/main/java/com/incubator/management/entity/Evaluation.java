package com.incubator.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

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
    private Startup startup;

    @ManyToOne
    @JoinColumn(name = "mentor_id", nullable = false)
    private User mentor;

    @Column(name = "technical_score")
    private Integer technicalScore;

    @Column(name = "market_score")
    private Integer marketScore;

    @Column(name = "financial_score")
    private Integer financialScore;

    @Column(name = "overall_score")
    private Integer overallScore;

    @Column(name = "technical_feedback", columnDefinition = "TEXT")
    private String technicalFeedback;

    @Column(name = "market_feedback", columnDefinition = "TEXT")
    private String marketFeedback;

    @Column(name = "financial_feedback", columnDefinition = "TEXT")
    private String financialFeedback;

    @Column(columnDefinition = "TEXT")
    private String recommendation;

    @CreationTimestamp
    @Column(name = "evaluated_at", updatable = false)
    private LocalDateTime evaluatedAt;
}
