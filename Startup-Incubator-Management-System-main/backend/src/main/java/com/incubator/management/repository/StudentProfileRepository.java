package com.incubator.management.repository;

import com.incubator.management.entity.StudentProfile;
import com.incubator.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Student Profile Repository
 * --------------------------
 * Provides database access for the StudentProfile entity.
 * JpaRepository gives standard CRUD methods out of the box.
 */
@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {

    /**
     * Find the student profile linked to a specific user.
     * Returns Optional — empty if the user has no student profile yet.
     */
    Optional<StudentProfile> findByUser(User user);
}
