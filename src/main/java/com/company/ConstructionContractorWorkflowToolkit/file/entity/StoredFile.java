package com.company.ConstructionContractorWorkflowToolkit.file.entity;

import com.company.ConstructionContractorWorkflowToolkit.file.storage.StorageProviderType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "stored_file")
public class StoredFile {

    @Id
    @Column(name = "stored_file_id", nullable = false)
    private UUID storedFileId;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "storage_provider",
            nullable = false,
            length = 40
    )
    private StorageProviderType storageProvider;

    @Column(
            name = "storage_container",
            length = 255
    )
    private String storageContainer;

    @Column(
            name = "storage_object_key",
            nullable = false,
            length = 1024
    )
    private String storageObjectKey;

    @Column(
            name = "provider_object_id",
            length = 512
    )
    private String providerObjectId;

    @Column(
            name = "provider_version_tag",
            length = 512
    )
    private String providerVersionTag;

    @Column(
            name = "original_file_name",
            nullable = false,
            length = 255
    )
    private String originalFileName;

    @Column(
            name = "content_type",
            length = 255
    )
    private String contentType;

    @Column(
            name = "size_bytes",
            nullable = false
    )
    private long sizeBytes;

    @Column(
            name = "sha256",
            length = 64
    )
    private String sha256;

    @Column(
            name = "uploaded_by",
            nullable = false
    )
    private UUID uploadedBy;

    @Column(
            name = "uploaded_at_utc",
            nullable = false
    )
    private LocalDateTime uploadedAtUtc;
}