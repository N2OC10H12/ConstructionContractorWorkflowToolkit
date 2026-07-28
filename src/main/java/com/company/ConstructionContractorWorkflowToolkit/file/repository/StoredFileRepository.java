package com.company.ConstructionContractorWorkflowToolkit.file.repository;

import com.company.ConstructionContractorWorkflowToolkit.file.entity.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StoredFileRepository
        extends JpaRepository<StoredFile, UUID> {
}