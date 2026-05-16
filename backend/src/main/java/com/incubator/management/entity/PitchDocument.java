package com.incubator.management.entity;

import jakarta.persistence.*;
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

    // ─── Constructors ─────────────────────────────────────────────────────────

    // Default (no-arg) constructor — required by JPA
    public PitchDocument() {}

    // All-args constructor for convenience
    public PitchDocument(Long id, Startup startup, User uploadedBy, String fileName,
                         String filePath, String fileType, Long fileSize,
                         LocalDateTime uploadedAt) {
        this.id = id;
        this.startup = startup;
        this.uploadedBy = uploadedBy;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.uploadedAt = uploadedAt;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public Startup getStartup() {
        return startup;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    // ─── Setters ──────────────────────────────────────────────────────────────

    public void setId(Long id) {
        this.id = id;
    }

    public void setStartup(Startup startup) {
        this.startup = startup;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    // ─── toString ─────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "PitchDocument{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize=" + fileSize +
                '}';
    }
}
