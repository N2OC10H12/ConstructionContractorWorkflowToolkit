package com.glassgang.pmworkflow.estimate.repository;

import com.glassgang.pmworkflow.estimate.entity.EstimatePdfTemplateVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EstimatePdfTemplateVersionRepository
        extends JpaRepository<EstimatePdfTemplateVersion, UUID> {
}