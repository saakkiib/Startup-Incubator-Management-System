package com.incubator.management.controller;

import com.incubator.management.entity.PitchDocument;
import com.incubator.management.service.PitchDocumentService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Pitch Document Controller
 * -------------------------
 * Handles all pitch deck / document upload API endpoints.
 * Base URL: /api/documents
 *
 * Endpoints:
 *   POST /api/documents              → Upload a new pitch document
 *   GET  /api/documents/startup/{id} → Get all documents for a specific startup
 */
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class PitchDocumentController {

    private final PitchDocumentService documentService;

    /**
     * Upload a pitch document for a startup.
     * File metadata (name, path, type, size) is sent in the request body.
     */
    @PostMapping
    public ResponseEntity<PitchDocument> upload(@RequestBody DocumentRequest request) {
        return ResponseEntity.ok(documentService.uploadDocument(
                request.getStartupId(),
                request.getUserId(),
                request.getFileName(),
                request.getFilePath(),
                request.getFileType(),
                request.getFileSize()
        ));
    }

    /**
     * Get all pitch documents associated with a given startup.
     */
    @GetMapping("/startup/{id}")
    public ResponseEntity<List<PitchDocument>> getByStartup(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentsByStartup(id));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Inner Request DTO
    // Used to deserialize the JSON body for the upload endpoint.
    // ─────────────────────────────────────────────────────────────────────────
    @Data
    public static class DocumentRequest {
        private Long startupId;   // Which startup this document belongs to
        private Long userId;      // Who uploaded it
        private String fileName;  // Original file name
        private String filePath;  // Server-side storage path
        private String fileType;  // e.g. "pdf", "pptx"
        private Long fileSize;    // Size in bytes
    }
}
