package com.incubator.management.controller;

import com.incubator.management.entity.PitchDocument;
import com.incubator.management.service.PitchDocumentService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class PitchDocumentController {

    private final PitchDocumentService documentService;

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

    @GetMapping("/startup/{id}")
    public ResponseEntity<List<PitchDocument>> getByStartup(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentsByStartup(id));
    }

    @Data
    public static class DocumentRequest {
        private Long startupId;
        private Long userId;
        private String fileName;
        private String filePath;
        private String fileType;
        private Long fileSize;
    }
}
