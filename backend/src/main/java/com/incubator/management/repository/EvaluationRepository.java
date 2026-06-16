package com.incubator.management.repository;

import com.incubator.management.entity.Evaluation;
import com.incubator.management.entity.Startup;
import com.incubator.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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

    /**
     * Get all evaluations submitted by a specific mentor.
     * Used on the mentor's submitted-evaluations page.
     */
    List<Evaluation> findByMentor(User mentor);

    @Modifying
    @Query("DELETE FROM Evaluation e WHERE e.mentor.id = :userId")
    void deleteByMentorId(Long userId);

    @Modifying
    @Query("DELETE FROM Evaluation e WHERE e.startup.id = :startupId")
    void deleteByStartupId(Long startupId);
}
