package com.incubator.management.controller;

import com.incubator.management.entity.Evaluation;
import com.incubator.management.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    @PostMapping
    public ResponseEntity<Evaluation> createEvaluation(@RequestBody EvaluationRequest request) {
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
        
        return ResponseEntity.ok(evaluationService.createEvaluation(evaluation, request.getStartupId(), request.getMentorId()));
    }

    @GetMapping("/startup/{id}")
    public ResponseEntity<List<Evaluation>> getByStartup(@PathVariable Long id) {
        return ResponseEntity.ok(evaluationService.getEvaluationsByStartup(id));
    }

    @lombok.Data
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
    }
}
