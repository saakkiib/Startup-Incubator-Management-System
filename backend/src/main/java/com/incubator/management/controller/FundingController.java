package com.incubator.management.controller;

import com.incubator.management.entity.Funding;
import com.incubator.management.service.FundingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Funding Controller
 * ------------------
 * Handles investment/funding transactions from investors to startups.
 * Base URL: /api/funding
 *
 * Endpoints:
 *   POST /api/funding              → Record a new funding transaction
 *   GET  /api/funding/startup/{id} → Get all funding records for a startup
 */
@RestController
@RequestMapping("/api/funding")
public class FundingController {

    private final FundingService fundingService;

    // Constructor injection — replaces Lombok @RequiredArgsConstructor
    public FundingController(FundingService fundingService) {
        this.fundingService = fundingService;
    }

    /**
     * Record a new funding deal between an investor and a startup.
     * Includes the investment amount, equity percentage, and deal terms.
     */
    @PostMapping
    public ResponseEntity<Funding> createFunding(@RequestBody FundingRequest request) {
        return ResponseEntity.ok(fundingService.createFunding(
                request.getStartupId(),
                request.getInvestorId(),
                request.getAmount(),
                request.getEquityPercentage(),
                request.getFundingType(),
                request.getTerms()
        ));
    }

    /**
     * Get all funding records associated with a specific startup.
     */
    @GetMapping("/startup/{id}")
    public ResponseEntity<List<Funding>> getByStartup(@PathVariable Long id) {
        return ResponseEntity.ok(fundingService.getFundingByStartup(id));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Inner Request DTO — with manual getters and setters
    // All fields needed to record a funding event.
    // ─────────────────────────────────────────────────────────────────────────
    public static class FundingRequest {

        private Long startupId;              // The startup receiving funding
        private Long investorId;             // The investor providing funds
        private BigDecimal amount;           // Total investment amount in USD
        private BigDecimal equityPercentage; // Equity given in return (e.g. 5.0 = 5%)
        private String fundingType;          // e.g. "Seed", "Series A"
        private String terms;                // Legal terms of the deal

        // Getters
        public Long getStartupId()                   { return startupId; }
        public Long getInvestorId()                  { return investorId; }
        public BigDecimal getAmount()                { return amount; }
        public BigDecimal getEquityPercentage()      { return equityPercentage; }
        public String getFundingType()               { return fundingType; }
        public String getTerms()                     { return terms; }

        // Setters
        public void setStartupId(Long startupId)                       { this.startupId = startupId; }
        public void setInvestorId(Long investorId)                     { this.investorId = investorId; }
        public void setAmount(BigDecimal amount)                       { this.amount = amount; }
        public void setEquityPercentage(BigDecimal equityPercentage)   { this.equityPercentage = equityPercentage; }
        public void setFundingType(String fundingType)                 { this.fundingType = fundingType; }
        public void setTerms(String terms)                             { this.terms = terms; }
    }
}
