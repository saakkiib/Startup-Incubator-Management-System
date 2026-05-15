package com.incubator.management.service;

import com.incubator.management.dto.StartupRequest;
import com.incubator.management.dto.StartupResponse;
import com.incubator.management.entity.Startup;
import com.incubator.management.entity.User;
import com.incubator.management.repository.StartupRepository;
import com.incubator.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StartupService {

    private final StartupRepository startupRepository;
    private final UserRepository userRepository;

    public StartupResponse createStartup(StartupRequest request) {
        User founder = userRepository.findById(request.getFounderId())
                .orElseThrow(() -> new RuntimeException("Founder not found"));

        Startup startup = Startup.builder()
                .name(request.getName())
                .description(request.getDescription())
                .founder(founder)
                .industry(request.getIndustry())
                .stage(request.getStage())
                .status("pending")
                .build();

        Startup savedStartup = startupRepository.save(startup);
        return mapToResponse(savedStartup);
    }

    public List<StartupResponse> getAllStartups() {
        return startupRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public StartupResponse approveStartup(Long id) {
        Startup startup = startupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Startup not found"));
        startup.setStatus("approved");
        return mapToResponse(startupRepository.save(startup));
    }

    public StartupResponse mapToResponse(Startup startup) {
        return StartupResponse.builder()
                .id(startup.getId())
                .name(startup.getName())
                .description(startup.getDescription())
                .founderName(startup.getFounder().getName())
                .industry(startup.getIndustry())
                .stage(startup.getStage())
                .status(startup.getStatus())
                .submittedAt(startup.getSubmittedAt())
                .build();
    }
}
