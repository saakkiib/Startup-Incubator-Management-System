package com.incubator.management.repository;

import com.incubator.management.entity.EditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EditLogRepository extends JpaRepository<EditLog, Long> {
    List<EditLog> findByUserIdOrderByCreatedAtDesc(Long userId);
}