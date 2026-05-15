package com.incubator.management.service;

import com.incubator.management.entity.PitchDocument;
import com.incubator.management.entity.Startup;
import com.incubator.management.entity.User;
import com.incubator.management.repository.PitchDocumentRepository;
import com.incubator.management.repository.StartupRepository;
import com.incubator.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PitchDocumentService {

    private final PitchDocumentRepository documentRepository;
    private final StartupRepository startupRepository;
    private final UserRepository userRepository;

    public PitchDocument uploadDocument(Long startupId, Long userId, String fileName, String filePath, String fileType, Long fileSize) {
        Startup startup = startupRepository.findById(startupId)
                .orElseThrow(() -> new RuntimeException("Startup not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PitchDocument document = PitchDocument.builder()
                .startup(startup)
                .uploadedBy(user)
                .fileName(fileName)
                .filePath(filePath)
                .fileType(fileType)
                .fileSize(fileSize)
                .build();

        return documentRepository.save(document);
    }

    public List<PitchDocument> getDocumentsByStartup(Long startupId) {
        Startup startup = startupRepository.findById(startupId)
                .orElseThrow(() -> new RuntimeException("Startup not found"));
        return documentRepository.findByStartup(startup);
    }
}
