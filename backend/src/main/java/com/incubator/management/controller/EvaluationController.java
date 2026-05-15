package com.incubator.management.controller;

import com.incubator.management.entity.Evaluation;
import com.incubator.management.service.EvaluationService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    /**
     * Submit a new evaluation for a startup.
     * Scores and feedback for technical, market, and financial aspects are included.
     */
    @PostMapping
    public ResponseEntity<Evaluation> createEvaluation(@RequestBody EvaluationRequest request) {
        // Build the Evaluation entity from the incoming request data
        Evaluation evaluation = Evaluation.builder()
                .technicalScore(request.getTechnicalScore())
                .marketScore(request.getMarketScore())
                .financialScore(request.getFinancialScore())
                .overallScore(request.getOverallScore())
                .technicalFeedback(request.getTechnicalFeedback())
                .marketFeedback(request.getMarketFeedback())
                .financialFeedback(request.getFinancialFeedback())
                .recommendation(request.getRecommendation())
                .build();

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
    // Inner Request DTO
    // Contains all fields needed to create an evaluation.
    // ─────────────────────────────────────────────────────────────────────────
    @lombok.Data
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
    }
}
