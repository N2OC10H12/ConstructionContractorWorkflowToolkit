package com.glassgang.pmworkflow.estimate.service;

import com.glassgang.pmworkflow.common.exception.NotFoundException;
import com.glassgang.pmworkflow.common.util.CurrentUserUtil;
import com.glassgang.pmworkflow.estimate.dto.dictionary.CompanyWorkTypeDivisionResponse;
import com.glassgang.pmworkflow.estimate.dto.dictionary.UpdateCompanyWorkTypeDivisionRequest;
import com.glassgang.pmworkflow.estimate.entity.CompanyWorkTypeDivision;
import com.glassgang.pmworkflow.estimate.repository.CompanyWorkTypeDivisionRepository;
import com.glassgang.pmworkflow.estimate.repository.CompanyWorkTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CompanyWorkTypeDivisionService {

    private final CompanyWorkTypeDivisionRepository divisionRepository;
    private final CompanyWorkTypeRepository companyWorkTypeRepository;
    private final EstimateAccessService estimateAccessService;
    private final CurrentUserUtil currentUserUtil;

    public CompanyWorkTypeDivisionService(
            CompanyWorkTypeDivisionRepository divisionRepository,
            CompanyWorkTypeRepository companyWorkTypeRepository,
            EstimateAccessService estimateAccessService,
            CurrentUserUtil currentUserUtil) {
        this.divisionRepository = divisionRepository;
        this.companyWorkTypeRepository = companyWorkTypeRepository;
        this.estimateAccessService = estimateAccessService;
        this.currentUserUtil = currentUserUtil;
    }

    @Transactional(readOnly = true)
    public List<CompanyWorkTypeDivisionResponse> getDivisions() {
        return divisionRepository.findAllByOrderByDivisionCodeAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CompanyWorkTypeDivisionResponse updateDivision(
            String divisionCode,
            UpdateCompanyWorkTypeDivisionRequest request) {

        estimateAccessService.requireEstimateDictionaryManageAccess();

        CompanyWorkTypeDivision division = divisionRepository
                .findByDivisionCode(divisionCode.trim())
                .orElseThrow(() -> new NotFoundException(
                        "Company Work Type division not found"));

        boolean currentlyEnabled =
                Boolean.TRUE.equals(division.getIsEnabled());

        boolean requestedEnabled =
                Boolean.TRUE.equals(request.getIsEnabled());

        LocalDateTime now = LocalDateTime.now();

        if (currentlyEnabled != requestedEnabled) {
            division.setIsEnabled(requestedEnabled);

            if (requestedEnabled) {
                UUID currentUserId = currentUserUtil.getCurrentUserId();

                division.setEnabledAtUtc(now);
                division.setEnabledByUserId(currentUserId);
            } else {
                division.setEnabledAtUtc(null);
                division.setEnabledByUserId(null);
            }
        }

        /*
         * Updated even for an idempotent request so the row records that the
         * configuration was processed without replacing the original
         * enabledAtUtc or enabledByUserId values.
         */
        division.setUpdatedAtUtc(now);

        CompanyWorkTypeDivision saved =
                divisionRepository.save(division);

        return toResponse(saved);
    }

    private CompanyWorkTypeDivisionResponse toResponse(
            CompanyWorkTypeDivision division) {

        CompanyWorkTypeDivisionResponse response =
                new CompanyWorkTypeDivisionResponse();

        response.setCompanyWorkTypeDivisionId(
                division.getCompanyWorkTypeDivisionId());

        response.setDivisionCode(division.getDivisionCode());
        response.setDivisionName(division.getDivisionName());
        response.setIsEnabled(division.getIsEnabled());
        response.setEnabledAtUtc(division.getEnabledAtUtc());
        response.setEnabledByUserId(division.getEnabledByUserId());

        response.setActiveWorkTypeCount(
                companyWorkTypeRepository
                        .countByDivisionCodeAndIsDeletedFalseAndIsActiveTrue(
                                division.getDivisionCode()));

        return response;
    }
}