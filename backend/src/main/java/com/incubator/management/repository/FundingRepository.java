package com.incubator.management.repository;

import com.incubator.management.entity.Funding;
import com.incubator.management.entity.Startup;
import com.incubator.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FundingRepository extends JpaRepository<Funding, Long> {
    List<Funding> findByStartup(Startup startup);
    List<Funding> findByInvestor(User investor);
}
