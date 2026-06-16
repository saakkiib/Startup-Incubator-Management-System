package com.incubator.management.controller;

import com.incubator.management.dto.FundingResponseDto;
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
 * Base URL: /api/fund
 *
 * Endpoints:
 *   POST /api/fund/invest          → Record a new funding transaction
 *   GET  /api/fund                 → Get all funding records (Admin)
 *   GET  /api/fund/startup/{id}    → Get all funding records for a startup
 */
@RestController
@RequestMapping("/api/fund")
public class FundingController {

    private final FundingService fundingService;

    public FundingController(FundingService fundingService) {
        this.fundingService = fundingService;
    }

    /**
     * Record a new funding deal between an investor and a startup.
     */
    @PostMapping("/invest")
    public ResponseEntity<Funding> investFunding(@RequestBody FundingRequest request) {
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
     * Get all funding records in the system (Admin overview).
     */
    @GetMapping
    public ResponseEntity<List<Funding>> getAllFunding() {
        return ResponseEntity.ok(fundingService.getAllFunding());
    }

    /**
     * Get all funding records associated with a specific startup.
     */
    @GetMapping("/startup/{id}")
    public ResponseEntity<List<Funding>> getByStartup(@PathVariable Long id) {
        return ResponseEntity.ok(fundingService.getFundingByStartup(id));
    }

    /**
     * Get all funding deals for startups owned by a specific founder.
     */
    @GetMapping("/founder/{founderId}")
    public ResponseEntity<List<Funding>> getByFounder(@PathVariable Long founderId) {
        return ResponseEntity.ok(fundingService.getFundingByFounder(founderId));
    }

    /**
     * Get all funding deals associated with a specific investor.
     */
    @GetMapping("/investor/{investorId}")
    public ResponseEntity<List<FundingResponseDto>> getByInvestor(@PathVariable Long investorId) {
        return ResponseEntity.ok(fundingService.getFundingByInvestor(investorId));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Inner Request DTO
    // ─────────────────────────────────────────────────────────────────────────
    public static class FundingRequest {
        private Long startupId;
        private Long investorId;
        private BigDecimal amount;
        private BigDecimal equityPercentage;
        private String fundingType;
        private String terms;

        public Long getStartupId()              { return startupId; }
        public Long getInvestorId()             { return investorId; }
        public BigDecimal getAmount()           { return amount; }
        public BigDecimal getEquityPercentage() { return equityPercentage; }
        public BigDecimal getEquity()           { return equityPercentage; }
        public String getFundingType()          { return fundingType; }
        public String getTerms()                { return terms; }
        public String getNotes()                { return terms; }

        public void setStartupId(Long startupId)                     { this.startupId = startupId; }
        public void setInvestorId(Long investorId)                   { this.investorId = investorId; }
        public void setAmount(BigDecimal amount)                     { this.amount = amount; }
        public void setEquityPercentage(BigDecimal equityPercentage) { this.equityPercentage = equityPercentage; }
        public void setEquity(BigDecimal equity)                     { this.equityPercentage = equity; }
        public void setFundingType(String fundingType)               { this.fundingType = fundingType; }
        public void setTerms(String terms)                           { this.terms = terms; }
        public void setNotes(String notes)                           { this.terms = notes; }
    }
}
