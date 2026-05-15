package com.incubator.management.repository;

import com.incubator.management.entity.MentorAssignment;
import com.incubator.management.entity.Startup;
import com.incubator.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MentorAssignmentRepository extends JpaRepository<MentorAssignment, Long> {
    List<MentorAssignment> findByStartup(Startup startup);
    List<MentorAssignment> findByMentor(User mentor);
}
