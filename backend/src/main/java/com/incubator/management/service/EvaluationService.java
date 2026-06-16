package com.incubator.management.service;

import com.incubator.management.entity.Evaluation;
import com.incubator.management.entity.Startup;
import com.incubator.management.entity.User;
import com.incubator.management.repository.EvaluationRepository;
import com.incubator.management.repository.StartupRepository;
import com.incubator.management.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Evaluation Service
 * ------------------
 * Handles mentor evaluations of startups:
 * - Saving a new evaluation with startup and mentor associations
 * - Fetching all evaluations for a given startup
 */
@Service
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final StartupRepository startupRepository;
    private final UserRepository userRepository;

    // Constructor injection — replaces Lombok @RequiredArgsConstructor
    public EvaluationService(EvaluationRepository evaluationRepository,
                             StartupRepository startupRepository,
                             UserRepository userRepository) {
        this.evaluationRepository = evaluationRepository;
        this.startupRepository = startupRepository;
        this.userRepository = userRepository;
    }

    /**
     * Save a new evaluation to the database.
     * Links the evaluation to both the startup being evaluated and the mentor submitting it.
     *
     * @param evaluation The pre-built evaluation entity with scores and feedback
     * @param startupId  The startup being evaluated
     * @param mentorId   The mentor performing the evaluation
     */
    public Evaluation createEvaluation(Evaluation evaluation, Long startupId, Long mentorId) {
        Startup startup = startupRepository.findById(startupId)
                .orElseThrow(() -> new RuntimeException("Startup not found"));
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new RuntimeException("Mentor not found"));

        // Attach the startup and mentor to the evaluation before saving
        evaluation.setStartup(startup);
        evaluation.setMentor(mentor);

        return evaluationRepository.save(evaluation);
    }

    /**
     * Fetch all evaluations submitted for a specific startup.
     */
    public List<Evaluation> getEvaluationsByStartup(Long startupId) {
        Startup startup = startupRepository.findById(startupId)
                .orElseThrow(() -> new RuntimeException("Startup not found"));
        return evaluationRepository.findByStartup(startup);
    }

    /**
     * Fetch all evaluations in the system.
     * Used by the Admin view-evaluations page.
     */
    public List<Evaluation> getAllEvaluations() {
        return evaluationRepository.findAll();
    }

    /**
     * Fetch all evaluations submitted by a specific mentor.
     * Used on the mentor's submitted-evaluations page.
     */
    public List<Evaluation> getEvaluationsByMentor(Long mentorId) {
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new RuntimeException("Mentor not found"));
        return evaluationRepository.findByMentor(mentor);
    }
}
