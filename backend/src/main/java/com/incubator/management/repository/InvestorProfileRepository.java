package com.incubator.management.repository;

import com.incubator.management.entity.InvestorProfile;
import com.incubator.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestorProfileRepository extends JpaRepository<InvestorProfile, Long> {
    Optional<InvestorProfile> findByUser(User user);
}
