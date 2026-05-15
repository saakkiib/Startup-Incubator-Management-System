package com.incubator.management.controller;

import com.incubator.management.entity.Funding;
import com.incubator.management.service.FundingService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/funding")
@RequiredArgsConstructor
public class FundingController {

    private final FundingService fundingService;

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

    @GetMapping("/startup/{id}")
    public ResponseEntity<List<Funding>> getByStartup(@PathVariable Long id) {
        return ResponseEntity.ok(fundingService.getFundingByStartup(id));
    }

    @Data
    public static class FundingRequest {
        private Long startupId;
        private Long investorId;
        private BigDecimal amount;
        private BigDecimal equityPercentage;
        private String fundingType;
        private String terms;
    }
}
