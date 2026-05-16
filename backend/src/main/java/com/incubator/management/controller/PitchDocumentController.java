package com.incubator.management.controller;

import com.incubator.management.entity.PitchDocument;
import com.incubator.management.service.PitchDocumentService;
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
public class PitchDocumentController {

    private final PitchDocumentService documentService;

    // Constructor injection — replaces Lombok @RequiredArgsConstructor
    public PitchDocumentController(PitchDocumentService documentService) {
        this.documentService = documentService;
    }

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
    // Inner Request DTO — with manual getters and setters
    // Used to deserialize the JSON body for the upload endpoint.
    // ─────────────────────────────────────────────────────────────────────────
    public static class DocumentRequest {

        private Long startupId;   // Which startup this document belongs to
        private Long userId;      // Who uploaded it
        private String fileName;  // Original file name
        private String filePath;  // Server-side storage path
        private String fileType;  // e.g. "pdf", "pptx"
        private Long fileSize;    // Size in bytes

        // Getters
        public Long getStartupId()    { return startupId; }
        public Long getUserId()       { return userId; }
        public String getFileName()   { return fileName; }
        public String getFilePath()   { return filePath; }
        public String getFileType()   { return fileType; }
        public Long getFileSize()     { return fileSize; }

        // Setters
        public void setStartupId(Long startupId)    { this.startupId = startupId; }
        public void setUserId(Long userId)          { this.userId = userId; }
        public void setFileName(String fileName)    { this.fileName = fileName; }
        public void setFilePath(String filePath)    { this.filePath = filePath; }
        public void setFileType(String fileType)    { this.fileType = fileType; }
        public void setFileSize(Long fileSize)      { this.fileSize = fileSize; }
    }
}
