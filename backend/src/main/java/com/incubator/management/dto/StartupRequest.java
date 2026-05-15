package com.incubator.management.dto;

import lombok.Data;

@Data
public class StartupRequest {
    private String name;
    private String description;
    private String industry;
    private String stage;
    private Long founderId;
}
