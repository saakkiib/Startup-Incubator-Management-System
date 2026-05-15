package com.incubator.management.service;

import com.incubator.management.entity.Funding;
import com.incubator.management.entity.Startup;
import com.incubator.management.entity.User;
import com.incubator.management.repository.FundingRepository;
import com.incubator.management.repository.StartupRepository;
import com.incubator.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Funding Service
 * ---------------
 * Handles all business logic for investment transactions:
 * - Recording a new funding deal between an investor and a startup
 * - Fetching all funding records for a given startup
 */
@Service
@RequiredArgsConstructor
public class FundingService {

    private final FundingRepository fundingRepository;
    private final StartupRepository startupRepository;
    private final UserRepository userRepository;

    /**
     * Record a new funding deal and save it to the database.
     * Validates that both the startup and investor exist before proceeding.
     * New funding records start with "completed" status.
     *
     * @param startupId  The startup receiving the investment
     * @param investorId The investor providing the funds
     * @param amount     Total dollar amount invested
     * @param equity     Equity percentage given in return
     * @param type       Funding round type (e.g. "Seed", "Series A")
     * @param terms      Legal terms agreed upon
     */
    public Funding createFunding(Long startupId, Long investorId, BigDecimal amount, BigDecimal equity, String type, String terms) {
        Startup startup = startupRepository.findById(startupId)
                .orElseThrow(() -> new RuntimeException("Startup not found"));
        User investor = userRepository.findById(investorId)
                .orElseThrow(() -> new RuntimeException("Investor not found"));

        Funding funding = Funding.builder()
                .startup(startup)
                .investor(investor)
                .amount(amount)
                .equityPercentage(equity)
                .fundingType(type)
                .terms(terms)
                .status("completed") // Mark as completed immediately on creation
                .build();

        return fundingRepository.save(funding);
    }

    /**
     * Retrieve all funding records associated with a specific startup.
     */
    public List<Funding> getFundingByStartup(Long startupId) {
        Startup startup = startupRepository.findById(startupId)
                .orElseThrow(() -> new RuntimeException("Startup not found"));
        return fundingRepository.findByStartup(startup);
    }
}
