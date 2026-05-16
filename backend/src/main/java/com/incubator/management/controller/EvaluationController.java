package com.incubator.management.controller;

import com.incubator.management.entity.Evaluation;
import com.incubator.management.service.EvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Evaluation Controller
 * ---------------------
 * Handles mentor evaluations of startups.
 * Base URL: /api/evaluations
 *
 * Endpoints:
 *   POST /api/evaluations              → Submit a new evaluation
 *   GET  /api/evaluations/startup/{id} → Get all evaluations for a startup
 */
@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

    private final EvaluationService evaluationService;

    // Constructor injection — replaces Lombok @RequiredArgsConstructor
    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    /**
     * Submit a new evaluation for a startup.
     * Scores and feedback for technical, market, and financial aspects are included.
     */
    @PostMapping
    public ResponseEntity<Evaluation> createEvaluation(@RequestBody EvaluationRequest request) {
        // Build the Evaluation entity from the incoming request data using setters
        Evaluation evaluation = new Evaluation();
        evaluation.setTechnicalScore(request.getTechnicalScore());
        evaluation.setMarketScore(request.getMarketScore());
        evaluation.setFinancialScore(request.getFinancialScore());
        evaluation.setOverallScore(request.getOverallScore());
        evaluation.setTechnicalFeedback(request.getTechnicalFeedback());
        evaluation.setMarketFeedback(request.getMarketFeedback());
        evaluation.setFinancialFeedback(request.getFinancialFeedback());
        evaluation.setRecommendation(request.getRecommendation());

        return ResponseEntity.ok(
                evaluationService.createEvaluation(evaluation, request.getStartupId(), request.getMentorId())
        );
    }

    /**
     * Fetch all evaluations submitted for a specific startup.
     */
    @GetMapping("/startup/{id}")
    public ResponseEntity<List<Evaluation>> getByStartup(@PathVariable Long id) {
        return ResponseEntity.ok(evaluationService.getEvaluationsByStartup(id));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Inner Request DTO — with manual getters and setters
    // Contains all fields needed to create an evaluation.
    // ─────────────────────────────────────────────────────────────────────────
    public static class EvaluationRequest {

        private Long startupId;            // Which startup is being evaluated
        private Long mentorId;             // Which mentor is submitting this evaluation
        private Integer technicalScore;    // Score out of 100 for technical viability
        private Integer marketScore;       // Score out of 100 for market potential
        private Integer financialScore;    // Score out of 100 for financial health
        private Integer overallScore;      // Aggregate score
        private String technicalFeedback;  // Detailed technical feedback
        private String marketFeedback;     // Detailed market feedback
        private String financialFeedback;  // Detailed financial feedback
        private String recommendation;     // Final recommendation (e.g. "fund", "reject")

        // Getters
        public Long getStartupId()             { return startupId; }
        public Long getMentorId()              { return mentorId; }
        public Integer getTechnicalScore()     { return technicalScore; }
        public Integer getMarketScore()        { return marketScore; }
        public Integer getFinancialScore()     { return financialScore; }
        public Integer getOverallScore()       { return overallScore; }
        public String getTechnicalFeedback()   { return technicalFeedback; }
        public String getMarketFeedback()      { return marketFeedback; }
        public String getFinancialFeedback()   { return financialFeedback; }
        public String getRecommendation()      { return recommendation; }

        // Setters
        public void setStartupId(Long startupId)                   { this.startupId = startupId; }
        public void setMentorId(Long mentorId)                     { this.mentorId = mentorId; }
        public void setTechnicalScore(Integer technicalScore)      { this.technicalScore = technicalScore; }
        public void setMarketScore(Integer marketScore)            { this.marketScore = marketScore; }
        public void setFinancialScore(Integer financialScore)      { this.financialScore = financialScore; }
        public void setOverallScore(Integer overallScore)          { this.overallScore = overallScore; }
        public void setTechnicalFeedback(String technicalFeedback) { this.technicalFeedback = technicalFeedback; }
        public void setMarketFeedback(String marketFeedback)       { this.marketFeedback = marketFeedback; }
        public void setFinancialFeedback(String financialFeedback) { this.financialFeedback = financialFeedback; }
        public void setRecommendation(String recommendation)       { this.recommendation = recommendation; }
    }
}
