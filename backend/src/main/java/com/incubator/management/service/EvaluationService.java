package com.incubator.management.service;

import com.incubator.management.entity.Evaluation;
import com.incubator.management.entity.Startup;
import com.incubator.management.entity.User;
import com.incubator.management.repository.EvaluationRepository;
import com.incubator.management.repository.StartupRepository;
import com.incubator.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final StartupRepository startupRepository;
    private final UserRepository userRepository;

    public Evaluation createEvaluation(Evaluation evaluation, Long startupId, Long mentorId) {
        Startup startup = startupRepository.findById(startupId)
                .orElseThrow(() -> new RuntimeException("Startup not found"));
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new RuntimeException("Mentor not found"));

        evaluation.setStartup(startup);
        evaluation.setMentor(mentor);

        return evaluationRepository.save(evaluation);
    }

    public List<Evaluation> getEvaluationsByStartup(Long startupId) {
        Startup startup = startupRepository.findById(startupId)
                .orElseThrow(() -> new RuntimeException("Startup not found"));
        return evaluationRepository.findByStartup(startup);
    }
}
