package com.incubator.management.controller;

import com.incubator.management.dto.PitchDocumentResponse;
import com.incubator.management.entity.PitchDocument;
import com.incubator.management.service.PitchDocumentService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public ResponseEntity<Map<String, Object>> upload(@RequestBody DocumentRequest request) {
        PitchDocument doc = documentService.uploadDocument(
                request.getStartupId(),
                request.getUserId(),
                request.getFileName(),
                request.getFilePath(),
                request.getFileType(),
                request.getFileSize(),
                request.getFileData()
        );
        Map<String, Object> result = new HashMap<>();
        result.put("id", doc.getId());
        result.put("fileName", doc.getFileName());
        result.put("fileType", doc.getFileType());
        result.put("fileSize", doc.getFileSize());
        result.put("uploadedAt", doc.getUploadedAt() != null ? doc.getUploadedAt().toString() : null);
        return ResponseEntity.ok(result);
    }

    /**
     * Get all pitch documents associated with a given startup (NO fileData — too large for lists).
     */
    @GetMapping("/startup/{id}")
    public ResponseEntity<List<PitchDocumentResponse>> getByStartup(@PathVariable Long id) {
        List<PitchDocument> docs = documentService.getDocumentsByStartup(id);
        List<PitchDocumentResponse> result = docs.stream().map(d -> new PitchDocumentResponse(
                d.getId(), d.getStartup().getId(),
                d.getUploadedBy().getId(), d.getUploadedBy().getFullName(),
                d.getFileName(), d.getFilePath(),
                d.getFileType(), d.getFileSize(), d.getUploadedAt()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * Get a single pitch document by its ID with full file data.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PitchDocument> getById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    /**
     * Download a pitch document as a file attachment.
     * Decodes the base64 fileData and streams it to the client.
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        PitchDocument doc = documentService.getDocumentById(id);
        byte[] fileBytes = Base64.getDecoder().decode(doc.getFileData());
        ByteArrayResource resource = new ByteArrayResource(fileBytes);

        String contentType = "application/octet-stream";
        if (doc.getFileType() != null) {
            switch (doc.getFileType().toLowerCase()) {
                case "pdf":  contentType = "application/pdf"; break;
                case "ppt":  contentType = "application/vnd.ms-powerpoint"; break;
                case "pptx": contentType = "application/vnd.openxmlformats-officedocument.presentationml.presentation"; break;
                case "doc":  contentType = "application/msword"; break;
                case "docx": contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"; break;
            }
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + doc.getFileName() + "\"")
                .body(resource);
    }

    /**
     * Delete a pitch document by its ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        documentService.deleteDocument(id);
        Map<String, String> result = new HashMap<>();
        result.put("message", "Document deleted successfully");
        return ResponseEntity.ok(result);
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
        private String fileData;  // Base64-encoded file content

        // Getters
        public Long getStartupId()    { return startupId; }
        public Long getUserId()       { return userId; }
        public String getFileName()   { return fileName; }
        public String getFilePath()   { return filePath; }
        public String getFileType()   { return fileType; }
        public Long getFileSize()     { return fileSize; }
        public String getFileData()   { return fileData; }

        // Setters
        public void setStartupId(Long startupId)    { this.startupId = startupId; }
        public void setUserId(Long userId)          { this.userId = userId; }
        public void setFileName(String fileName)    { this.fileName = fileName; }
        public void setFilePath(String filePath)    { this.filePath = filePath; }
        public void setFileType(String fileType)    { this.fileType = fileType; }
        public void setFileSize(Long fileSize)      { this.fileSize = fileSize; }
        public void setFileData(String fileData)    { this.fileData = fileData; }
    }
}
