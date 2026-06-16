package com.incubator.management.repository;

import com.incubator.management.entity.Startup;
import com.incubator.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Startup Repository
 * ------------------
 * Provides database access for the Startup entity.
 * JpaRepository gives standard CRUD methods out of the box.
 */
@Repository
public interface StartupRepository extends JpaRepository<Startup, Long> {

    /**
     * Get all startups created by a specific founder.
     * Used to show a student their own startup list.
     */
    List<Startup> findByFounder(User founder);

    /**
     * Get all startups with a given approval status.
     * Useful for filtering: e.g. findByStatus("pending") for admin review queue.
     */
    List<Startup> findByStatus(String status);
}
