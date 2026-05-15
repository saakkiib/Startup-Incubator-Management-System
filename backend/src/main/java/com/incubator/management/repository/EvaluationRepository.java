package com.incubator.management.repository;

import com.incubator.management.entity.Evaluation;
import com.incubator.management.entity.Startup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findByStartup(Startup startup);
}
