package com.incubator.management.service;

import com.incubator.management.dto.FundingResponseDto;
import com.incubator.management.entity.Funding;
import com.incubator.management.entity.Startup;
import com.incubator.management.entity.User;
import com.incubator.management.repository.FundingRepository;
import com.incubator.management.repository.StartupRepository;
import com.incubator.management.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Funding Service
 * ---------------
 * Handles all business logic for investment transactions:
 * - Recording a new funding deal between an investor and a startup
 * - Fetching all funding records for a given startup
 * - Fetching all investments made by a specific investor
 */
@Service
public class FundingService {

    private final FundingRepository fundingRepository;
    private final StartupRepository startupRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // Constructor injection — replaces Lombok @RequiredArgsConstructor
    public FundingService(FundingRepository fundingRepository,
                          StartupRepository startupRepository,
                          UserRepository userRepository,
                          NotificationService notificationService) {
        this.fundingRepository = fundingRepository;
        this.startupRepository = startupRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
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

        // Update startup's current funding
        BigDecimal current = startup.getCurrentFunding();
        if (current == null) {
            current = BigDecimal.ZERO;
        }
        startup.setCurrentFunding(current.add(amount));
        startupRepository.save(startup);

        Funding saved = fundingRepository.save(funding);

        // Notify startup founder
        User founder = startup.getFounder();
        if (founder != null) {
            notificationService.createNotification(
                    founder.getId(),
                    "💰 Funding Received!",
                    "Your startup \"" + startup.getName() + "\" has received $" + amount + " from " + investor.getFullName() + ".",
                    "FUNDING_RECEIVED",
                    startup.getId()
            );
        }

        return saved;
    }

    /**
     * Retrieve all funding records associated with a specific startup.
     */
    public List<Funding> getFundingByStartup(Long startupId) {
        Startup startup = startupRepository.findById(startupId)
                .orElseThrow(() -> new RuntimeException("Startup not found"));
        return fundingRepository.findByStartup(startup);
    }

    /**
     * Retrieve all funding records in the system.
     * Used by the Admin funding-records page.
     */
    public List<Funding> getAllFunding() {
        return fundingRepository.findAll();
    }

    /**
     * Retrieve all funding records for any startup owned by a specific founder.
     */
    public List<Funding> getFundingByFounder(Long founderId) {
        User founder = userRepository.findById(founderId)
                .orElseThrow(() -> new RuntimeException("Founder not found"));
        return fundingRepository.findByStartupFounder(founder);
    }

    /**
     * Retrieve all investments made by a specific investor.
     * Used by the investor dashboard, history, and profile pages.
     *
     * @param investorId The investor user's ID
     * @return List of FundingResponseDto (flat JSON, no circular references)
     */
    public List<FundingResponseDto> getFundingByInvestor(Long investorId) {
        User investor = userRepository.findById(investorId)
                .orElseThrow(() -> new RuntimeException("Investor not found"));
        List<Funding> deals = fundingRepository.findByInvestor(investor);
        return deals.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    /**
     * Convert a Funding entity into a flat FundingResponseDto.
     * This avoids Jackson circular serialization errors caused by
     * the Funding→Startup→Founder and Funding→User entity relationships.
     */
    public FundingResponseDto mapToDto(Funding funding) {
        FundingResponseDto dto = new FundingResponseDto();
        dto.setId(funding.getId());
        dto.setAmount(funding.getAmount());
        dto.setEquityPercentage(funding.getEquityPercentage());
        dto.setFundingType(funding.getFundingType());
        dto.setTerms(funding.getTerms());
        dto.setStatus(funding.getStatus());
        dto.setInvestedAt(funding.getFundedAt());

        if (funding.getStartup() != null) {
            dto.setStartupId(funding.getStartup().getId());
            dto.setStartupName(funding.getStartup().getName());
        }
        if (funding.getInvestor() != null) {
            dto.setInvestorId(funding.getInvestor().getId());
            dto.setInvestorName(funding.getInvestor().getFullName());
        }
        return dto;
    }
}
