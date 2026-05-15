package com.incubator.management.repository;

import com.incubator.management.entity.PitchDocument;
import com.incubator.management.entity.Startup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PitchDocumentRepository extends JpaRepository<PitchDocument, Long> {
    List<PitchDocument> findByStartup(Startup startup);
}
