package com.company.ConstructionContractorWorkflowToolkit.estimate.pdf;

import com.company.ConstructionContractorWorkflowToolkit.common.exception.NotFoundException;
import com.company.ConstructionContractorWorkflowToolkit.common.util.CurrentUserUtil;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.pdf.EstimatePdfTemplateResponse;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.pdf.UpdateEstimatePdfTemplateRequest;
import com.company.ConstructionContractorWorkflowToolkit.estimate.entity.EstimatePdfTemplate;
import com.company.ConstructionContractorWorkflowToolkit.estimate.repository.EstimatePdfTemplateRepository;
import com.company.ConstructionContractorWorkflowToolkit.estimate.entity.EstimatePdfTemplateVersion;
import com.company.ConstructionContractorWorkflowToolkit.estimate.repository.EstimatePdfTemplateVersionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EstimatePdfTemplateService {

    private static final String DEFAULT_TEMPLATE_CODE = "DEFAULT_ESTIMATE_TEMPLATE";

    private final EstimatePdfTemplateRepository estimatePdfTemplateRepository;
    private final EstimatePdfTemplateAccessService estimatePdfTemplateAccessService;
    private final CurrentUserUtil currentUserUtil;
    private final EstimatePdfTemplateVersionRepository estimatePdfTemplateVersionRepository;
    private final EstimatePdfTemplateValidationService estimatePdfTemplateValidationService;

    public EstimatePdfTemplateService(
            EstimatePdfTemplateRepository estimatePdfTemplateRepository,
            EstimatePdfTemplateVersionRepository estimatePdfTemplateVersionRepository,
            EstimatePdfTemplateAccessService estimatePdfTemplateAccessService,
            EstimatePdfTemplateValidationService estimatePdfTemplateValidationService,
            CurrentUserUtil currentUserUtil) {
        this.estimatePdfTemplateRepository = estimatePdfTemplateRepository;
        this.estimatePdfTemplateVersionRepository = estimatePdfTemplateVersionRepository;
        this.estimatePdfTemplateAccessService = estimatePdfTemplateAccessService;
        this.estimatePdfTemplateValidationService = estimatePdfTemplateValidationService;
        this.currentUserUtil = currentUserUtil;
    }

    @Transactional(readOnly = true)
    public EstimatePdfTemplateResponse getDefaultTemplate() {

        estimatePdfTemplateAccessService.requireAdminAccess();
        EstimatePdfTemplate template = getDefaultTemplateEntity();

        return toResponse(template);
    }

    @Transactional
    public EstimatePdfTemplateResponse updateDefaultTemplate(UpdateEstimatePdfTemplateRequest request) {
        estimatePdfTemplateAccessService.requireAdminAccess();
        estimatePdfTemplateValidationService.validateUpdate(request);

        EstimatePdfTemplate template = getDefaultTemplateEntity();

        int nextVersionNumber = template.getVersionNumber() == null
                ? 1
                : template.getVersionNumber() + 1;

        template.setName(request.getName().trim());
        template.setHtmlTemplate(request.getHtmlTemplate());
        template.setCssTemplate(request.getCssTemplate());
        template.setTemplateDefinitionJson(request.getTemplateDefinitionJson());
        template.setVersionNumber(nextVersionNumber);

        template.setIsActive(request.getIsActive());

        template.setUpdatedAtUtc(LocalDateTime.now());
        template.setUpdatedByUserId(currentUserUtil.getCurrentUserId());

        EstimatePdfTemplate saved = estimatePdfTemplateRepository.save(template);

        createVersionSnapshot(saved, request.getChangeNote());

        return toResponse(saved);
    }

    private EstimatePdfTemplate getDefaultTemplateEntity() {
        return estimatePdfTemplateRepository
                .findByCodeAndIsDeletedFalse(DEFAULT_TEMPLATE_CODE)
                .orElseThrow(() -> new NotFoundException("Default estimate PDF template not found"));
    }

    private EstimatePdfTemplateResponse toResponse(EstimatePdfTemplate template) {
        EstimatePdfTemplateResponse response = new EstimatePdfTemplateResponse();

        response.setEstimatePdfTemplateId(template.getEstimatePdfTemplateId());
        response.setCode(template.getCode());
        response.setName(template.getName());
        response.setHtmlTemplate(template.getHtmlTemplate());
        response.setCssTemplate(template.getCssTemplate());
        response.setIsDefault(template.getIsDefault());
        response.setIsActive(template.getIsActive());
        response.setCreatedAtUtc(template.getCreatedAtUtc());
        response.setUpdatedAtUtc(template.getUpdatedAtUtc());
        response.setTemplateDefinitionJson(template.getTemplateDefinitionJson());
        response.setVersionNumber(
                template.getVersionNumber() != null ? template.getVersionNumber() : 1);

        return response;
    }

    private void createVersionSnapshot(
            EstimatePdfTemplate template,
            String changeNote) {
        EstimatePdfTemplateVersion version = new EstimatePdfTemplateVersion();

        version.setEstimatePdfTemplateVersionId(UUID.randomUUID());
        version.setEstimatePdfTemplateId(template.getEstimatePdfTemplateId());
        version.setVersionNumber(template.getVersionNumber());
        version.setName(template.getName());
        version.setHtmlTemplate(template.getHtmlTemplate());
        version.setCssTemplate(template.getCssTemplate());
        version.setTemplateDefinitionJson(template.getTemplateDefinitionJson());
        version.setIsActive(template.getIsActive());
        version.setCreatedAtUtc(LocalDateTime.now());
        version.setCreatedByUserId(currentUserUtil.getCurrentUserId());
        version.setChangeNote(changeNote);

        estimatePdfTemplateVersionRepository.save(version);
    }
}