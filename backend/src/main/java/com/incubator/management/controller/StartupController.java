package com.incubator.management.controller;

import com.incubator.management.dto.StartupRequest;
import com.incubator.management.dto.StartupResponse;
import com.incubator.management.service.StartupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/startups")
@RequiredArgsConstructor
public class StartupController {

    private final StartupService startupService;

    @PostMapping
    public ResponseEntity<StartupResponse> createStartup(@RequestBody StartupRequest request) {
        return ResponseEntity.ok(startupService.createStartup(request));
    }

    @GetMapping
    public ResponseEntity<List<StartupResponse>> getAllStartups() {
        return ResponseEntity.ok(startupService.getAllStartups());
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<StartupResponse> approveStartup(@PathVariable Long id) {
        return ResponseEntity.ok(startupService.approveStartup(id));
    }
}
