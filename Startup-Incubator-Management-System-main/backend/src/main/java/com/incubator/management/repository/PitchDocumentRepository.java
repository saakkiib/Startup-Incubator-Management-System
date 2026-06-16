package com.incubator.management.repository;

import com.incubator.management.entity.PitchDocument;
import com.incubator.management.entity.Startup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Pitch Document Repository
 * -------------------------
 * Provides database access for the PitchDocument entity.
 * JpaRepository gives standard CRUD methods out of the box.
 */
@Repository
public interface PitchDocumentRepository extends JpaRepository<PitchDocument, Long> {

    /**
     * Get all pitch documents uploaded for a specific startup.
     * A startup can have multiple documents (e.g. pitch deck, financial model, executive summary).
     */
    List<PitchDocument> findByStartup(Startup startup);

    @Modifying
    @Query("DELETE FROM PitchDocument p WHERE p.uploadedBy.id = :userId")
    void deleteByUploadedByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM PitchDocument p WHERE p.startup.id = :startupId")
    void deleteByStartupId(Long startupId);
}
