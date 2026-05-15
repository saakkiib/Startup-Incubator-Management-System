package com.incubator.management.repository;

import com.incubator.management.entity.MentorProfile;
import com.incubator.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Mentor Profile Repository
 * -------------------------
 * Provides database access for the MentorProfile entity.
 * JpaRepository gives standard CRUD methods out of the box.
 */
@Repository
public interface MentorProfileRepository extends JpaRepository<MentorProfile, Long> {

    /**
     * Find the mentor profile linked to a specific user.
     * Returns Optional — empty if the user has no mentor profile yet.
     */
    Optional<MentorProfile> findByUser(User user);
}
