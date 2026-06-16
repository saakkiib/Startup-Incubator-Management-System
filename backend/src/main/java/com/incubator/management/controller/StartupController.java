package com.incubator.management.controller;

import com.incubator.management.dto.StartupRequest;
import com.incubator.management.dto.StartupResponse;
import com.incubator.management.service.StartupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Startup Controller
 * ------------------
 * Handles all startup management API endpoints.
 * Base URL: /api/startups
 *
 * Endpoints:
 *   POST /api/startups              → Create a new startup
 *   GET  /api/startups              → Fetch all startups
 *   PUT  /api/startups/{id}/approve → Approve a startup (Admin)
 *   PUT  /api/startups/{id}/reject  → Reject a startup (Admin)
 */
@RestController
@RequestMapping("/api/startups")
public class StartupController {

    private final StartupService startupService;

    public StartupController(StartupService startupService) {
        this.startupService = startupService;
    }

    /**
     * Create a new startup submission.
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
     * Retrieve all startups created by a specific founder.
     */
    @GetMapping("/founder/{founderId}")
    public ResponseEntity<List<StartupResponse>> getStartupsByFounder(@PathVariable Long founderId) {
        return ResponseEntity.ok(startupService.getStartupsByFounder(founderId));
    }

    /**
     * Retrieve a single startup by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StartupResponse> getStartupById(@PathVariable Long id) {
        return ResponseEntity.ok(startupService.getStartupById(id));
    }

    /**
     * Update startup progress.
     */
    @PutMapping("/{id}/progress")
    public ResponseEntity<StartupResponse> updateProgress(@PathVariable Long id, @RequestBody java.util.Map<String, Integer> payload) {
        Integer progress = payload.get("progress");
        return ResponseEntity.ok(startupService.updateProgress(id, progress));
    }

    /**
     * Approve a startup by its ID.
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<StartupResponse> approveStartup(@PathVariable Long id) {
        return ResponseEntity.ok(startupService.approveStartup(id));
    }

    /**
     * Reject a startup by its ID with a reason.
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<StartupResponse> rejectStartup(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String reason = payload.getOrDefault("reason", "");
        return ResponseEntity.ok(startupService.rejectStartup(id, reason));
    }

    /**
     * Delete a startup by its ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStartup(@PathVariable Long id) {
        startupService.deleteStartup(id);
        return ResponseEntity.noContent().build();
    }
}
