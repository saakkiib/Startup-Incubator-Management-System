package com.incubator.management.service;

import com.incubator.management.entity.PitchDocument;
import com.incubator.management.entity.Startup;
import com.incubator.management.entity.User;
import com.incubator.management.repository.PitchDocumentRepository;
import com.incubator.management.repository.StartupRepository;
import com.incubator.management.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Pitch Document Service
 * ----------------------
 * Handles pitch deck and document management:
 * - Saving a new document record (metadata only, not the actual file binary)
 * - Fetching all documents uploaded for a specific startup
 */
@Service
public class PitchDocumentService {

    private final PitchDocumentRepository documentRepository;
    private final StartupRepository startupRepository;
    private final UserRepository userRepository;

    // Constructor injection — replaces Lombok @RequiredArgsConstructor
    public PitchDocumentService(PitchDocumentRepository documentRepository,
                                StartupRepository startupRepository,
                                UserRepository userRepository) {
        this.documentRepository = documentRepository;
        this.startupRepository = startupRepository;
        this.userRepository = userRepository;
    }

    /**
     * Save a pitch document record to the database.
     * Note: This stores metadata (name, path, type, size) — not the raw file bytes.
     * The actual file should be handled separately (e.g. via cloud storage).
     *
     * @param startupId The startup this document belongs to
     * @param userId    The user who uploaded it
     * @param fileName  Original file name (e.g. "pitch_deck.pdf")
     * @param filePath  Server-side path or URL to the file
     * @param fileType  MIME type or extension (e.g. "pdf")
     * @param fileSize  Size of the file in bytes
     */
    public PitchDocument uploadDocument(Long startupId, Long userId, String fileName,
                                        String filePath, String fileType, Long fileSize) {
        Startup startup = startupRepository.findById(startupId)
                .orElseThrow(() -> new RuntimeException("Startup not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Build PitchDocument using setters instead of @Builder
        PitchDocument document = new PitchDocument();
        document.setStartup(startup);
        document.setUploadedBy(user);
        document.setFileName(fileName);
        document.setFilePath(filePath);
        document.setFileType(fileType);
        document.setFileSize(fileSize);

        return documentRepository.save(document);
    }

    /**
     * Retrieve all pitch documents uploaded for a specific startup.
     */
    public List<PitchDocument> getDocumentsByStartup(Long startupId) {
        Startup startup = startupRepository.findById(startupId)
                .orElseThrow(() -> new RuntimeException("Startup not found"));
        return documentRepository.findByStartup(startup);
    }
}
