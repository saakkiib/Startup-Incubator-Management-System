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

@Service
@RequiredArgsConstructor
public class FundingService {

    private final FundingRepository fundingRepository;
    private final StartupRepository startupRepository;
    private final UserRepository userRepository;

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
                .status("completed")
                .build();

        return fundingRepository.save(funding);
    }

    public List<Funding> getFundingByStartup(Long startupId) {
        Startup startup = startupRepository.findById(startupId)
                .orElseThrow(() -> new RuntimeException("Startup not found"));
        return fundingRepository.findByStartup(startup);
    }
}
