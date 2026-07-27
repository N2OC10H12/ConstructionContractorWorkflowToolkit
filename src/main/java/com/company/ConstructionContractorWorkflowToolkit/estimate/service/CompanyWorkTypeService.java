package com.company.ConstructionContractorWorkflowToolkit.estimate.service;

import com.company.ConstructionContractorWorkflowToolkit.common.exception.BusinessRuleException;
import com.company.ConstructionContractorWorkflowToolkit.common.exception.NotFoundException;
import com.company.ConstructionContractorWorkflowToolkit.common.util.CurrentUserUtil;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.dictionary.CompanyWorkTypeResponse;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.dictionary.CompanyWorkTypeSearchResultResponse;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.dictionary.CreateCompanyWorkTypeRequest;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.dictionary.UpdateCompanyWorkTypeRequest;
import com.company.ConstructionContractorWorkflowToolkit.estimate.entity.CompanyWorkType;
import com.company.ConstructionContractorWorkflowToolkit.estimate.entity.CompanyWorkTypeDivision;
import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.CompanyWorkTypeSourceType;
import com.company.ConstructionContractorWorkflowToolkit.estimate.repository.CompanyWorkTypeDivisionRepository;
import com.company.ConstructionContractorWorkflowToolkit.estimate.repository.CompanyWorkTypeRepository;
import com.company.ConstructionContractorWorkflowToolkit.estimate.util.CompanyWorkTypeCodeUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CompanyWorkTypeService {

    private static final int DEFAULT_SEARCH_LIMIT = 20;
    private static final int MAX_SEARCH_LIMIT = 50;
    private static final int MAX_SEARCH_QUERY_LENGTH = 200;

    private final CompanyWorkTypeRepository companyWorkTypeRepository;
    private final CompanyWorkTypeDivisionRepository divisionRepository;
    private final EstimateAccessService estimateAccessService;
    private final CurrentUserUtil currentUserUtil;

    public CompanyWorkTypeService(
            CompanyWorkTypeRepository companyWorkTypeRepository,
            CompanyWorkTypeDivisionRepository divisionRepository,
            EstimateAccessService estimateAccessService,
            CurrentUserUtil currentUserUtil) {
        this.companyWorkTypeRepository = companyWorkTypeRepository;
        this.divisionRepository = divisionRepository;
        this.estimateAccessService = estimateAccessService;
        this.currentUserUtil = currentUserUtil;
    }

    @Transactional(readOnly = true)
    public List<CompanyWorkTypeResponse> getWorkTypes(
            String divisionCode) {

        List<CompanyWorkType> workTypes;

        if (divisionCode == null || divisionCode.isBlank()) {
            workTypes = companyWorkTypeRepository
                    .findByIsDeletedFalseOrderByDisplayOrderAsc();
        } else {
            String normalizedDivisionCode = normalizeDivisionCode(divisionCode);

            requireDivision(normalizedDivisionCode);

            workTypes = companyWorkTypeRepository
                    .findByDivisionCodeAndIsDeletedFalseOrderByDisplayOrderAsc(
                            normalizedDivisionCode);
        }

        Map<String, CompanyWorkTypeDivision> divisionsByCode = getDivisionsByCode();

        Map<UUID, CompanyWorkType> workTypesById = workTypes.stream()
                .collect(Collectors.toMap(
                        CompanyWorkType::getCompanyWorkTypeId,
                        Function.identity()));

        return workTypes.stream()
                .map(workType -> toResponse(
                        workType,
                        divisionsByCode,
                        workTypesById))
                .toList();
    }

    @Transactional(readOnly = true)
    public CompanyWorkTypeResponse getWorkType(
            UUID companyWorkTypeId) {

        CompanyWorkType workType = requireWorkType(companyWorkTypeId);

        return toResponse(workType);
    }

    @Transactional(readOnly = true)
    public List<CompanyWorkTypeSearchResultResponse> searchSelectableWorkTypes(
            String query,
            Integer limit) {

        String textQuery = normalizeSearchQuery(query);
        String codeQuery = normalizePartialCodeQuery(textQuery);
        int resolvedLimit = resolveSearchLimit(limit);

        Map<String, CompanyWorkTypeDivision> divisionsByCode = getDivisionsByCode();

        return companyWorkTypeRepository
                .searchSelectableWorkTypes(
                        textQuery,
                        codeQuery,
                        PageRequest.of(0, resolvedLimit))
                .stream()
                .map(workType -> toSearchResultResponse(
                        workType,
                        divisionsByCode))
                .toList();
    }

    @Transactional(readOnly = true)
    public CompanyWorkType getSelectableWorkType(
            UUID companyWorkTypeId) {

        CompanyWorkType workType = requireWorkType(
                companyWorkTypeId);

        if (!Boolean.TRUE.equals(workType.getIsActive())) {
            throw new BusinessRuleException(
                    "Company work type is inactive");
        }

        if (!isSelectable(workType)) {
            throw new BusinessRuleException(
                    "Company work type is not selectable");
        }

        CompanyWorkTypeDivision division = requireDivision(
                workType.getDivisionCode());

        if (!Boolean.TRUE.equals(division.getIsEnabled())) {
            throw new BusinessRuleException(
                    "Company work type division is disabled");
        }

        return workType;
    }

    @Transactional
    public CompanyWorkTypeResponse createWorkType(
            CreateCompanyWorkTypeRequest request) {

        estimateAccessService.requireEstimateDictionaryManageAccess();

        String normalizedCode = normalizeWorkTypeCode(request.getCode());

        String formattedCode = CompanyWorkTypeCodeUtil.format(normalizedCode);

        if (companyWorkTypeRepository
                .existsByNormalizedCodeAndIsDeletedFalse(
                        normalizedCode)) {
            throw duplicateCodeException(formattedCode);
        }

        String divisionCode = CompanyWorkTypeCodeUtil.deriveDivisionCode(
                normalizedCode);

        requireDivision(divisionCode);

        int level = CompanyWorkTypeCodeUtil.deriveCustomLevel(
                normalizedCode);

        CompanyWorkType parentWorkType = resolveParent(
                request.getParentWorkTypeId(),
                null,
                divisionCode,
                level);

        LocalDateTime now = LocalDateTime.now();

        CompanyWorkType workType = new CompanyWorkType();

        workType.setCompanyWorkTypeId(UUID.randomUUID());
        workType.setCode(formattedCode);
        workType.setNormalizedCode(normalizedCode);
        workType.setName(requireName(request.getName()));

        workType.setLevel(level);
        workType.setDivisionCode(divisionCode);
        workType.setParentWorkType(parentWorkType);

        workType.setSourceType(
                CompanyWorkTypeSourceType.COMPANY_CUSTOM);
        workType.setSourceEdition(null);
        workType.setOriginalName(null);

        workType.setSearchAliases(
                normalizeOptionalText(request.getSearchAliases()));

        workType.setDisplayOrder(nextDisplayOrder());

        workType.setIsActive(
                request.getIsActive() != null
                        ? request.getIsActive()
                        : true);

        workType.setIsDeleted(false);

        workType.setCreatedAtUtc(now);
        workType.setUpdatedAtUtc(now);
        workType.setDeletedAtUtc(null);
        workType.setDeletedByUserId(null);

        return toResponse(
                companyWorkTypeRepository.save(workType));
    }

    @Transactional
    public CompanyWorkTypeResponse updateWorkType(
            UUID companyWorkTypeId,
            UpdateCompanyWorkTypeRequest request) {

        estimateAccessService.requireEstimateDictionaryManageAccess();

        CompanyWorkType workType = requireWorkType(companyWorkTypeId);

        if (Boolean.TRUE.equals(request.getClearParentWorkType())
                && request.getParentWorkTypeId() != null) {
            throw new BusinessRuleException(
                    "parentWorkTypeId cannot be provided when "
                            + "clearParentWorkType is true");
        }

        String targetNormalizedCode = workType.getNormalizedCode();

        String targetFormattedCode = workType.getCode();

        String targetDivisionCode = workType.getDivisionCode();

        int targetLevel = workType.getLevel();

        if (request.getCode() != null) {
            targetNormalizedCode = normalizeWorkTypeCode(request.getCode());

            targetFormattedCode = CompanyWorkTypeCodeUtil.format(
                    targetNormalizedCode);

            boolean codeChanged = !targetNormalizedCode.equals(
                    workType.getNormalizedCode());

            if (codeChanged
                    && companyWorkTypeRepository
                            .existsByNormalizedCodeAndIsDeletedFalseAndCompanyWorkTypeIdNot(
                                    targetNormalizedCode,
                                    companyWorkTypeId)) {
                throw duplicateCodeException(targetFormattedCode);
            }

            if (codeChanged && hasChildren(companyWorkTypeId)) {
                throw new BusinessRuleException(
                        "The work type code cannot be changed because "
                                + "the work type has child work types");
            }

            targetDivisionCode = CompanyWorkTypeCodeUtil.deriveDivisionCode(
                    targetNormalizedCode);

            requireDivision(targetDivisionCode);

            /*
             * Imported hierarchy levels came from MasterFormat.
             * Only custom rows use the custom level convention.
             */
            if (workType.getSourceType() == CompanyWorkTypeSourceType.COMPANY_CUSTOM) {
                targetLevel = CompanyWorkTypeCodeUtil.deriveCustomLevel(
                        targetNormalizedCode);
            }
        }

        CompanyWorkType targetParent = workType.getParentWorkType();

        if (Boolean.TRUE.equals(
                request.getClearParentWorkType())) {
            targetParent = null;
        } else if (request.getParentWorkTypeId() != null) {
            targetParent = resolveParent(
                    request.getParentWorkTypeId(),
                    companyWorkTypeId,
                    targetDivisionCode,
                    targetLevel);
        } else if (targetParent != null) {
            /*
             * Revalidate the existing parent when code, division,
             * or level may have changed.
             */
            targetParent = validateParent(
                    targetParent,
                    companyWorkTypeId,
                    targetDivisionCode,
                    targetLevel);
        }

        workType.setCode(targetFormattedCode);
        workType.setNormalizedCode(targetNormalizedCode);
        workType.setDivisionCode(targetDivisionCode);
        workType.setLevel(targetLevel);
        workType.setParentWorkType(targetParent);

        if (request.getName() != null) {
            workType.setName(
                    requireName(request.getName()));
        }

        if (request.getSearchAliases() != null) {
            workType.setSearchAliases(
                    normalizeOptionalText(
                            request.getSearchAliases()));
        }

        if (request.getIsActive() != null) {
            workType.setIsActive(
                    request.getIsActive());
        }

        workType.setUpdatedAtUtc(LocalDateTime.now());

        return toResponse(
                companyWorkTypeRepository.save(workType));
    }

    @Transactional
    public CompanyWorkTypeResponse restoreOriginalName(
            UUID companyWorkTypeId) {

        estimateAccessService.requireEstimateDictionaryManageAccess();

        CompanyWorkType workType = requireWorkType(companyWorkTypeId);

        if (workType.getSourceType() != CompanyWorkTypeSourceType.MASTERFORMAT_IMPORT
                || workType.getOriginalName() == null
                || workType.getOriginalName().isBlank()) {
            throw new BusinessRuleException(
                    "Original name is not available for this work type");
        }

        workType.setName(workType.getOriginalName());
        workType.setUpdatedAtUtc(LocalDateTime.now());

        return toResponse(
                companyWorkTypeRepository.save(workType));
    }

    @Transactional
    public void deleteWorkType(
            UUID companyWorkTypeId) {

        estimateAccessService.requireEstimateDictionaryManageAccess();

        CompanyWorkType workType = requireWorkType(companyWorkTypeId);

        if (hasChildren(companyWorkTypeId)) {
            throw new BusinessRuleException(
                    "The work type cannot be deleted because "
                            + "it has child work types");
        }

        LocalDateTime now = LocalDateTime.now();

        workType.setIsActive(false);
        workType.setIsDeleted(true);
        workType.setDeletedAtUtc(now);
        workType.setDeletedByUserId(
                currentUserUtil.getCurrentUserId());
        workType.setUpdatedAtUtc(now);

        companyWorkTypeRepository.save(workType);
    }

    private CompanyWorkType requireWorkType(
            UUID companyWorkTypeId) {

        return companyWorkTypeRepository
                .findByCompanyWorkTypeIdAndIsDeletedFalse(
                        companyWorkTypeId)
                .orElseThrow(() -> new NotFoundException(
                        "Company work type not found"));
    }

    private CompanyWorkTypeDivision requireDivision(
            String divisionCode) {

        return divisionRepository
                .findByDivisionCode(divisionCode)
                .orElseThrow(() -> new NotFoundException(
                        "Company Work Type division not found"));
    }

    private CompanyWorkType resolveParent(
            UUID parentWorkTypeId,
            UUID childWorkTypeId,
            String divisionCode,
            int childLevel) {

        if (parentWorkTypeId == null) {
            return null;
        }

        CompanyWorkType parentWorkType = requireWorkType(parentWorkTypeId);

        return validateParent(
                parentWorkType,
                childWorkTypeId,
                divisionCode,
                childLevel);
    }

    private CompanyWorkType validateParent(
            CompanyWorkType parentWorkType,
            UUID childWorkTypeId,
            String divisionCode,
            int childLevel) {

        if (childWorkTypeId != null
                && childWorkTypeId.equals(
                        parentWorkType.getCompanyWorkTypeId())) {
            throw new BusinessRuleException(
                    "A work type cannot be its own parent");
        }

        if (!divisionCode.equals(
                parentWorkType.getDivisionCode())) {
            throw new BusinessRuleException(
                    "Parent work type must belong to the same division");
        }

        if (parentWorkType.getLevel() >= childLevel) {
            throw new BusinessRuleException(
                    "Parent work type level must be lower than "
                            + "the child work type level");
        }

        return parentWorkType;
    }

    private boolean hasChildren(
            UUID companyWorkTypeId) {

        return companyWorkTypeRepository
                .existsByParentWorkType_CompanyWorkTypeIdAndIsDeletedFalse(
                        companyWorkTypeId);
    }

    private int nextDisplayOrder() {
        Integer maximumDisplayOrder = companyWorkTypeRepository
                .findMaximumActiveDisplayOrder();

        if (maximumDisplayOrder == null) {
            return 0;
        }

        return Math.addExact(maximumDisplayOrder, 1);
    }

    private Map<String, CompanyWorkTypeDivision> getDivisionsByCode() {

        return divisionRepository
                .findAllByOrderByDivisionCodeAsc()
                .stream()
                .collect(Collectors.toMap(
                        CompanyWorkTypeDivision::getDivisionCode,
                        Function.identity()));
    }

    private CompanyWorkTypeResponse toResponse(
            CompanyWorkType workType) {

        CompanyWorkTypeDivision division = requireDivision(workType.getDivisionCode());

        return toResponse(
                workType,
                Map.of(
                        division.getDivisionCode(),
                        division),
                Map.of());
    }

    private CompanyWorkTypeResponse toResponse(
            CompanyWorkType workType,
            Map<String, CompanyWorkTypeDivision> divisionsByCode,
            Map<UUID, CompanyWorkType> workTypesById) {

        CompanyWorkTypeResponse response = new CompanyWorkTypeResponse();

        response.setCompanyWorkTypeId(
                workType.getCompanyWorkTypeId());

        response.setCode(workType.getCode());
        response.setName(workType.getName());
        response.setLevel(workType.getLevel());

        response.setDivisionCode(
                workType.getDivisionCode());

        CompanyWorkTypeDivision division = divisionsByCode.get(
                workType.getDivisionCode());

        if (division != null) {
            response.setDivisionName(
                    division.getDivisionName());
        }

        CompanyWorkType parent = workType.getParentWorkType();

        if (parent != null) {
            CompanyWorkType mappedParent = workTypesById.getOrDefault(
                    parent.getCompanyWorkTypeId(),
                    parent);

            response.setParentWorkTypeId(
                    mappedParent.getCompanyWorkTypeId());

            response.setParentWorkTypeCode(
                    mappedParent.getCode());

            response.setParentWorkTypeName(
                    mappedParent.getName());
        }

        response.setSourceType(
                workType.getSourceType());

        response.setSourceEdition(
                workType.getSourceEdition());

        response.setOriginalName(
                workType.getOriginalName());

        response.setSearchAliases(
                workType.getSearchAliases());

        response.setDisplayOrder(
                workType.getDisplayOrder());

        response.setIsActive(
                workType.getIsActive());

        return response;
    }

    private CompanyWorkTypeSearchResultResponse toSearchResultResponse(
            CompanyWorkType workType,
            Map<String, CompanyWorkTypeDivision> divisionsByCode) {

        CompanyWorkTypeSearchResultResponse response = new CompanyWorkTypeSearchResultResponse();

        response.setCompanyWorkTypeId(
                workType.getCompanyWorkTypeId());

        response.setCode(workType.getCode());
        response.setName(workType.getName());

        response.setLevel(workType.getLevel());
        response.setIsSelectable(isSelectable(workType));

        response.setDivisionCode(
                workType.getDivisionCode());

        CompanyWorkTypeDivision division = divisionsByCode.get(
                workType.getDivisionCode());

        if (division != null) {
            response.setDivisionName(
                    division.getDivisionName());
        }

        response.setDisplayLabel(
                workType.getCode()
                        + " — "
                        + workType.getName());

        return response;
    }

    private String normalizeWorkTypeCode(
            String code) {

        try {
            return CompanyWorkTypeCodeUtil.normalize(code);
        } catch (IllegalArgumentException exception) {
            throw new BusinessRuleException(
                    exception.getMessage());
        }
    }

    private String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessRuleException(
                    "Work type name is required");
        }

        return name.trim();
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private String normalizeDivisionCode(
            String divisionCode) {

        String normalized = divisionCode.trim();

        if (!normalized.matches("\\d{2}")) {
            throw new BusinessRuleException(
                    "Division code must contain exactly two digits");
        }

        return normalized;
    }

    private String normalizeSearchQuery(
            String query) {

        if (query == null || query.isBlank()) {
            return "";
        }

        String normalized = query.trim();

        if (normalized.length() > MAX_SEARCH_QUERY_LENGTH) {
            throw new BusinessRuleException(
                    "Search query must be <= "
                            + MAX_SEARCH_QUERY_LENGTH
                            + " characters");
        }

        return normalized;
    }

    private String normalizePartialCodeQuery(
            String query) {

        if (query.isBlank()
                || !query.matches("[0-9\\s.\\-]+")) {
            return "";
        }

        return query.replaceAll("[^0-9]", "");
    }

    private int resolveSearchLimit(Integer limit) {
        if (limit == null) {
            return DEFAULT_SEARCH_LIMIT;
        }

        if (limit < 1) {
            throw new BusinessRuleException(
                    "Search limit must be at least 1");
        }

        return Math.min(limit, MAX_SEARCH_LIMIT);
    }

    private BusinessRuleException duplicateCodeException(
            String formattedCode) {

        return new BusinessRuleException(
                "A company work type with code "
                        + formattedCode
                        + " already exists.");
    }

    private boolean isSelectable(
            CompanyWorkType workType) {

        Integer level = workType.getLevel();

        return level != null
                && level >= 4
                && level <= 5;
    }
}