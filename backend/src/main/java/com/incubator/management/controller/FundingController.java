package com.incubator.management.controller;

import com.incubator.management.entity.Funding;
import com.incubator.management.service.FundingService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class FundingController {

    private final FundingService fundingService;

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
    // Inner Request DTO
    // All fields needed to record a funding event.
    // ─────────────────────────────────────────────────────────────────────────
    @Data
    public static class FundingRequest {
        private Long startupId;             // The startup receiving funding
        private Long investorId;            // The investor providing funds
        private BigDecimal amount;          // Total investment amount in USD
        private BigDecimal equityPercentage;// Equity given in return (e.g. 5.0 = 5%)
        private String fundingType;         // e.g. "Seed", "Series A"
        private String terms;              // Legal terms of the deal
    }
}
