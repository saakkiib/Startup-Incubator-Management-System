package com.incubator.management.controller;

import com.incubator.management.dto.StartupRequest;
import com.incubator.management.dto.StartupResponse;
import com.incubator.management.service.StartupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Startup Controller
 * ------------------
 * Handles all startup management API endpoints.
 * Base URL: /api/startups
 *
 * Endpoints:
 *   POST /api/startups            → Create a new startup
 *   GET  /api/startups            → Fetch all startups
 *   PUT  /api/startups/{id}/approve → Approve a startup (Admin/Mentor)
 */
@RestController
@RequestMapping("/api/startups")
@RequiredArgsConstructor
public class StartupController {

    private final StartupService startupService;

    /**
     * Create a new startup submission.
     * The founder ID and startup details are included in the request body.
     */
    @PostMapping
    public ResponseEntity<StartupResponse> createStartup(@RequestBody StartupRequest request) {
        return ResponseEntity.ok(startupService.createStartup(request));
    }

    /**
     * Retrieve a list of all startups in the system.
     */
    @GetMapping
    public ResponseEntity<List<StartupResponse>> getAllStartups() {
        return ResponseEntity.ok(startupService.getAllStartups());
    }

    /**
     * Approve a startup by its ID.
     * Typically called by an Admin or Mentor to greenlight a startup.
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<StartupResponse> approveStartup(@PathVariable Long id) {
        return ResponseEntity.ok(startupService.approveStartup(id));
    }
}
