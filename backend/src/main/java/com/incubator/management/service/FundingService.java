package com.incubator.management.service;

import com.incubator.management.entity.Funding;
import com.incubator.management.entity.Startup;
import com.incubator.management.entity.User;
import com.incubator.management.repository.FundingRepository;
import com.incubator.management.repository.StartupRepository;
import com.incubator.management.repository.UserRepository;
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
public class FundingService {

    private final FundingRepository fundingRepository;
    private final StartupRepository startupRepository;
    private final UserRepository userRepository;

    // Constructor injection — replaces Lombok @RequiredArgsConstructor
    public FundingService(FundingRepository fundingRepository,
                          StartupRepository startupRepository,
                          UserRepository userRepository) {
        this.fundingRepository = fundingRepository;
        this.startupRepository = startupRepository;
        this.userRepository = userRepository;
    }

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
    public Funding createFunding(Long startupId, Long investorId, BigDecimal amount,
                                 BigDecimal equity, String type, String terms) {
        Startup startup = startupRepository.findById(startupId)
                .orElseThrow(() -> new RuntimeException("Startup not found"));
        User investor = userRepository.findById(investorId)
                .orElseThrow(() -> new RuntimeException("Investor not found"));

        // Build Funding object using setters instead of @Builder
        Funding funding = new Funding();
        funding.setStartup(startup);
        funding.setInvestor(investor);
        funding.setAmount(amount);
        funding.setEquityPercentage(equity);
        funding.setFundingType(type);
        funding.setTerms(terms);
        funding.setStatus("completed"); // Mark as completed immediately on creation

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
