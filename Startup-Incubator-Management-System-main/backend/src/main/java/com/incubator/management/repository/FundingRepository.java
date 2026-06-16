package com.incubator.management.repository;

import com.incubator.management.entity.Funding;
import com.incubator.management.entity.Startup;
import com.incubator.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Funding Repository
 * ------------------
 * Provides database access for the Funding entity.
 * JpaRepository gives standard CRUD methods out of the box.
 */
@Repository
public interface FundingRepository extends JpaRepository<Funding, Long> {

    /**
     * Get all funding records for a specific startup.
     * Shows the complete investment history for that startup.
     */
    List<Funding> findByStartup(Startup startup);

    /**
     * Get all investments made by a specific investor.
     * Used on the investor dashboard to show their portfolio.
     */
    List<Funding> findByInvestor(User investor);

    /**
     * Get all investments received by startups of a specific founder.
     */
    List<Funding> findByStartupFounder(User founder);

    @Modifying
    @Query("DELETE FROM Funding f WHERE f.investor.id = :userId")
    void deleteByInvestorId(Long userId);

    @Modifying
    @Query("DELETE FROM Funding f WHERE f.startup.id = :startupId")
    void deleteByStartupId(Long startupId);
}
