package com.incubator.management.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StartupResponse {
    private Long id;
    private String name;
    private String description;
    private String founderName;
    private String industry;
    private String stage;
    private String status;
    private LocalDateTime submittedAt;
}
