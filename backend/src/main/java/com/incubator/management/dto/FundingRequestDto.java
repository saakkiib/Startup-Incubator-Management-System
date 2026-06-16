package com.incubator.management.dto;

import java.math.BigDecimal;

public class FundingRequestDto {

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
