package com.incubator.management.repository;

import com.incubator.management.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByUserIdOrderByCreatedAtDesc(Long userId);
}