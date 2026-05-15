package com.incubator.management.repository;

import com.incubator.management.entity.Startup;
import com.incubator.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StartupRepository extends JpaRepository<Startup, Long> {
    List<Startup> findByFounder(User founder);
    List<Startup> findByStatus(String status);
}
