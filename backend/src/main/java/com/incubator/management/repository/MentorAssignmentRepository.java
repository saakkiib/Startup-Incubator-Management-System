package com.incubator.management.repository;

import com.incubator.management.entity.MentorAssignment;
import com.incubator.management.entity.Startup;
import com.incubator.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Mentor Assignment Repository
 * ----------------------------
 * Provides database access for the MentorAssignment entity.
 * JpaRepository gives standard CRUD methods out of the box.
 */
@Repository
public interface MentorAssignmentRepository extends JpaRepository<MentorAssignment, Long> {

    /**
     * Get all mentor assignments for a specific startup.
     * A startup can have multiple mentors assigned over its lifecycle.
     */
    List<MentorAssignment> findByStartup(Startup startup);

    /**
     * Get all assignments where a specific mentor is involved.
     * Used on the mentor dashboard to show their assigned startups.
     */
    List<MentorAssignment> findByMentor(User mentor);
}
