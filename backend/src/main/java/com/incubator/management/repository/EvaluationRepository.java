package com.incubator.management.repository;

import com.incubator.management.entity.Evaluation;
import com.incubator.management.entity.Startup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Evaluation Repository
 * ---------------------
 * Provides database access for the Evaluation entity.
 * JpaRepository gives standard CRUD methods out of the box.
 */
@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    /**
     * Get all evaluations submitted for a specific startup.
     * A startup can be evaluated multiple times by different mentors.
     */
    List<Evaluation> findByStartup(Startup startup);
}
