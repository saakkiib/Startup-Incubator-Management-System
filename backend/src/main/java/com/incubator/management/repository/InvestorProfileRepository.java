package com.incubator.management.repository;

import com.incubator.management.entity.InvestorProfile;
import com.incubator.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Investor Profile Repository
 * ---------------------------
 * Provides database access for the InvestorProfile entity.
 * JpaRepository gives standard CRUD methods out of the box.
 */
@Repository
public interface InvestorProfileRepository extends JpaRepository<InvestorProfile, Long> {

    /**
     * Find the investor profile linked to a specific user.
     * Returns Optional — empty if the user has no investor profile yet.
     */
    Optional<InvestorProfile> findByUser(User user);
}
