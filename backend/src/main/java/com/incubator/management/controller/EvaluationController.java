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
 *   POST /api/evaluations                → Submit a new evaluation
 *   GET  /api/evaluations                → Get all evaluations (Admin)
 *   GET  /api/evaluations/startup/{id}   → Get all evaluations for a startup
 *   GET  /api/evaluations/mentor/{id}    → Get all evaluations by a mentor
 */
@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    /**
     * Submit a new evaluation for a startup.
     */
    @PostMapping
    public ResponseEntity<Evaluation> createEvaluation(@RequestBody EvaluationRequest request) {
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
     * Get all evaluations in the system (Admin overview).
     */
    @GetMapping
    public ResponseEntity<List<Evaluation>> getAllEvaluations() {
        return ResponseEntity.ok(evaluationService.getAllEvaluations());
    }

    /**
     * Fetch all evaluations submitted for a specific startup.
     */
    @GetMapping("/startup/{id}")
    public ResponseEntity<List<Evaluation>> getByStartup(@PathVariable Long id) {
        return ResponseEntity.ok(evaluationService.getEvaluationsByStartup(id));
    }

    /**
     * Fetch all evaluations submitted by a specific mentor.
     * Used on the mentor's submitted-evaluations page.
     */
    @GetMapping("/mentor/{mentorId}")
    public ResponseEntity<List<Evaluation>> getByMentor(@PathVariable Long mentorId) {
        return ResponseEntity.ok(evaluationService.getEvaluationsByMentor(mentorId));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Inner Request DTO
    // ─────────────────────────────────────────────────────────────────────────
    public static class EvaluationRequest {
        private Long startupId;
        private Long mentorId;
        private Integer technicalScore;
        private Integer marketScore;
        private Integer financialScore;
        private Integer overallScore;
        private String technicalFeedback;
        private String marketFeedback;
        private String financialFeedback;
        private String recommendation;

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
