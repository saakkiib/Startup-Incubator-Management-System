package com.incubator.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * PitchDocument Entity
 * --------------------
 * Maps to the "pitch_documents" table.
 * Stores metadata about pitch decks and supporting documents uploaded for a startup.
 * Note: Only file metadata is stored here — the actual file lives in cloud/local storage.
 */
@Entity
@Table(name = "pitch_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PitchDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "startup_id", nullable = false)
    private Startup startup;              // The startup this document belongs to

    @ManyToOne
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;              // The user who uploaded this document

    @Column(name = "file_name", nullable = false)
    private String fileName;              // Original filename (e.g. "pitch_deck_v2.pdf")

    @Column(name = "file_path", nullable = false)
    private String filePath;              // Server path or cloud URL to the actual file

    @Column(name = "file_type")
    private String fileType;              // Extension/MIME type (e.g. "pdf", "pptx")

    @Column(name = "file_size")
    private Long fileSize;                // File size in bytes

    @CreationTimestamp
    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;     // When the document was uploaded
}
