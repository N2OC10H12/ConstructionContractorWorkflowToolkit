package com.company.ConstructionContractorWorkflowToolkit.company.service;

import com.company.ConstructionContractorWorkflowToolkit.company.dto.CompanyProfileResponse;
import com.company.ConstructionContractorWorkflowToolkit.company.dto.UpdateCompanyProfileRequest;
import com.company.ConstructionContractorWorkflowToolkit.company.entity.CompanyProfile;
import com.company.ConstructionContractorWorkflowToolkit.company.repository.CompanyProfileRepository;
import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.BidRoundingMode;
import com.company.ConstructionContractorWorkflowToolkit.file.entity.StoredFile;
import com.company.ConstructionContractorWorkflowToolkit.file.service.StoredFileService;
import com.company.ConstructionContractorWorkflowToolkit.file.storage.StoredObjectContent;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class CompanyProfileService {

    private static final String DEFAULT_PROFILE_CODE = "DEFAULT";

    private static final long MAX_LOGO_SIZE_BYTES = 5L * 1024L * 1024L;

    private static final String LOGO_URL = "/api/company/profile/logo";

    private static final Logger log = LoggerFactory.getLogger(CompanyProfileService.class);

    private static final Set<String> ALLOWED_LOGO_EXTENSIONS = Set.of(
            "png", "jpg", "jpeg", "webp", "bmp");

    private static final Set<String> ALLOWED_LOGO_CONTENT_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/jpg",
            "image/webp",
            "image/bmp",
            "image/x-ms-bmp");

    private static final Set<String> ALLOWED_PATCH_FIELDS = Set.of(
            "syncToken",

            "companyName",
            "legalName",
            "employerId",
            "country",

            "companyAddressLine1",
            "companyAddressLine2",
            "companyCity",
            "companyState",
            "companyPostalCode",
            "companyCountry",

            "legalAddressLine1",
            "legalAddressLine2",
            "legalCity",
            "legalState",
            "legalPostalCode",
            "legalCountry",

            "customerCommunicationAddressLine1",
            "customerCommunicationAddressLine2",
            "customerCommunicationCity",
            "customerCommunicationState",
            "customerCommunicationPostalCode",
            "customerCommunicationCountry",

            "primaryPhone",
            "email",
            "website",
            "introductionData",
            "defaultBidRoundingMode");

    private final CompanyProfileRepository companyProfileRepository;
    private final StoredFileService storedFileService;

    /*
     * Temporary dependency for a logo created before the StoredFile migration.
     * Remove after legacy logo columns and paths have been retired.
     */

    public record CompanyLogoResource(
            Resource resource,
            MediaType mediaType,
            String filename) {
    }

    @Transactional(readOnly = true)
    public CompanyProfileResponse getDefaultProfile() {
        return toResponse(getDefaultProfileEntity());
    }

    @Transactional(readOnly = true)
    public BidRoundingMode getDefaultBidRoundingMode() {
        BidRoundingMode roundingMode = getDefaultProfileEntity()
                .getDefaultBidRoundingMode();

        return roundingMode != null
                ? roundingMode
                : BidRoundingMode.WHOLE;
    }

    @Transactional
    public CompanyProfileResponse updateDefaultProfile(UpdateCompanyProfileRequest request) {
        if (request == null) {
            throw badRequest("Request body is required");
        }

        validateUnknownFields(request);

        Integer requestSyncToken = readRequiredSyncToken(request);

        CompanyProfile profile = getDefaultProfileEntity();

        if (!requestSyncToken.equals(profile.getSyncToken())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Company profile was updated by another user. Reload and try again.");
        }

        applyRequiredString(request, "companyName", profile::setCompanyName, 255);

        applyOptionalString(request, "legalName", profile::setLegalName, 255);
        applyOptionalString(request, "employerId", profile::setEmployerId, 50);
        applyOptionalString(request, "country", profile::setCountry, 100);

        applyOptionalString(request, "companyAddressLine1", profile::setCompanyAddressLine1, 255);
        applyOptionalString(request, "companyAddressLine2", profile::setCompanyAddressLine2, 255);
        applyOptionalString(request, "companyCity", profile::setCompanyCity, 100);
        applyOptionalString(request, "companyState", profile::setCompanyState, 100);
        applyOptionalString(request, "companyPostalCode", profile::setCompanyPostalCode, 50);
        applyOptionalString(request, "companyCountry", profile::setCompanyCountry, 100);

        applyOptionalString(request, "legalAddressLine1", profile::setLegalAddressLine1, 255);
        applyOptionalString(request, "legalAddressLine2", profile::setLegalAddressLine2, 255);
        applyOptionalString(request, "legalCity", profile::setLegalCity, 100);
        applyOptionalString(request, "legalState", profile::setLegalState, 100);
        applyOptionalString(request, "legalPostalCode", profile::setLegalPostalCode, 50);
        applyOptionalString(request, "legalCountry", profile::setLegalCountry, 100);

        applyOptionalString(request, "customerCommunicationAddressLine1", profile::setCustomerCommunicationAddressLine1,
                255);
        applyOptionalString(request, "customerCommunicationAddressLine2", profile::setCustomerCommunicationAddressLine2,
                255);
        applyOptionalString(request, "customerCommunicationCity", profile::setCustomerCommunicationCity, 100);
        applyOptionalString(request, "customerCommunicationState", profile::setCustomerCommunicationState, 100);
        applyOptionalString(request, "customerCommunicationPostalCode", profile::setCustomerCommunicationPostalCode,
                50);
        applyOptionalString(request, "customerCommunicationCountry", profile::setCustomerCommunicationCountry, 100);

        applyOptionalString(request, "primaryPhone", profile::setPrimaryPhone, 50);
        applyOptionalString(request, "email", profile::setEmail, 255);
        applyOptionalString(request, "website", profile::setWebsite, 255);
        applyOptionalString(
                request,
                "introductionData",
                profile::setIntroductionData,
                1500);

        applyDefaultBidRoundingMode(
                request,
                profile);

        profile.setSyncToken(profile.getSyncToken() + 1);
        profile.setUpdatedAtUtc(LocalDateTime.now());

        return toResponse(companyProfileRepository.save(profile));
    }

    @Transactional
    public CompanyProfileResponse uploadLogo(
            MultipartFile file,
            Integer syncToken) {

        CompanyProfile profile = getDefaultProfileEntity();

        validateSyncToken(profile, syncToken);
        validateLogoFile(file);

        StoredFile previousStoredFile = profile.getLogoStoredFile();

        UUID storedFileId = UUID.randomUUID();

        String objectKey = "company-profiles/"
                + profile.getCompanyProfileId()
                + "/logos/"
                + storedFileId;

        StoredFile newStoredFile = storedFileService.store(
                storedFileId,
                objectKey,
                file);

        profile.setLogoStoredFile(newStoredFile);
        profile.setSyncToken(profile.getSyncToken() + 1);
        profile.setUpdatedAtUtc(LocalDateTime.now());

        companyProfileRepository.saveAndFlush(profile);

        deletePreviousReusableLogo(previousStoredFile);

        return toResponse(profile);
    }

    @Transactional(readOnly = true)
    public CompanyLogoResource getLogoResource() {
        CompanyProfile profile = getDefaultProfileEntity();

        StoredFile storedFile = profile.getLogoStoredFile();

        if (storedFile == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Company logo not found");
        }

        StoredObjectContent content = storedFileService.loadContent(storedFile);

        return new CompanyLogoResource(
                new InputStreamResource(content.inputStream()),
                resolveMediaType(storedFile.getContentType()),
                normalizeLogoResponseFilename(
                        storedFile.getOriginalFileName()));
    }

    @Transactional
    public CompanyProfileResponse deleteLogo(Integer syncToken) {
        CompanyProfile profile = getDefaultProfileEntity();

        validateSyncToken(profile, syncToken);

        StoredFile previousStoredFile = profile.getLogoStoredFile();

        profile.setLogoStoredFile(null);
        profile.setSyncToken(profile.getSyncToken() + 1);
        profile.setUpdatedAtUtc(LocalDateTime.now());

        companyProfileRepository.saveAndFlush(profile);

        deletePreviousReusableLogo(previousStoredFile);

        return toResponse(profile);
    }

    @Transactional(readOnly = true)
    public String getLogoDataUrl() {
        CompanyProfile profile = getDefaultProfileEntity();

        StoredFile storedFile = profile.getLogoStoredFile();

        if (storedFile == null) {
            return null;
        }

        try (StoredObjectContent content = storedFileService.loadContent(storedFile)) {

            byte[] bytes = content.inputStream().readAllBytes();

            String contentType = normalizeLogoDataUrlContentType(
                    storedFile.getContentType());

            return "data:"
                    + contentType
                    + ";base64,"
                    + Base64.getEncoder().encodeToString(bytes);

        } catch (IOException | RuntimeException exception) {
            log.warn(
                    "Unable to load company logo StoredFile {} for PDF rendering",
                    storedFile.getStoredFileId(),
                    exception);

            return null;
        }
    }

    private CompanyProfile getDefaultProfileEntity() {
        return companyProfileRepository
                .findByProfileCodeAndIsActiveTrueAndIsDeletedFalse(DEFAULT_PROFILE_CODE)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Default company profile not found"));
    }

    private void validateUnknownFields(UpdateCompanyProfileRequest request) {
        for (String field : request.keySet()) {
            if (!ALLOWED_PATCH_FIELDS.contains(field)) {
                throw badRequest("Unknown company profile field: " + field);
            }
        }
    }

    private Integer readRequiredSyncToken(UpdateCompanyProfileRequest request) {
        if (!request.containsKey("syncToken") || request.get("syncToken") == null) {
            throw badRequest("syncToken is required");
        }

        Object value = request.get("syncToken");

        if (value instanceof Number number) {
            return number.intValue();
        }

        if (value instanceof String stringValue) {
            try {
                return Integer.parseInt(stringValue);
            } catch (NumberFormatException ex) {
                throw badRequest("syncToken must be a number");
            }
        }

        throw badRequest("syncToken must be a number");
    }

    private void applyRequiredString(
            UpdateCompanyProfileRequest request,
            String field,
            Consumer<String> setter,
            int maxLength) {
        if (!request.containsKey(field)) {
            return;
        }

        Object rawValue = request.get(field);

        if (rawValue == null) {
            throw badRequest(field + " cannot be null");
        }

        String value = normalizeString(rawValue);

        if (value == null) {
            throw badRequest(field + " cannot be blank");
        }

        validateMaxLength(field, value, maxLength);
        setter.accept(value);
    }

    private void applyOptionalString(
            UpdateCompanyProfileRequest request,
            String field,
            Consumer<String> setter,
            int maxLength) {
        if (!request.containsKey(field)) {
            return;
        }

        Object rawValue = request.get(field);

        if (rawValue == null) {
            setter.accept(null);
            return;
        }

        String value = normalizeString(rawValue);

        if (value == null) {
            setter.accept(null);
            return;
        }

        validateMaxLength(field, value, maxLength);
        setter.accept(value);
    }

    private void applyDefaultBidRoundingMode(
            UpdateCompanyProfileRequest request,
            CompanyProfile profile) {

        String field = "defaultBidRoundingMode";

        if (!request.containsKey(field)) {
            return;
        }

        Object rawValue = request.get(field);

        if (rawValue == null) {
            throw badRequest(field + " cannot be null");
        }

        String value = normalizeString(rawValue);

        if (value == null) {
            throw badRequest(field + " cannot be blank");
        }

        try {
            profile.setDefaultBidRoundingMode(
                    BidRoundingMode.valueOf(
                            value.toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException exception) {
            throw badRequest(
                    field + " must be WHOLE or FRACTIONAL");
        }
    }

    private String normalizeString(Object rawValue) {
        String value = String.valueOf(rawValue).trim();
        return value.isBlank() ? null : value;
    }

    private void validateMaxLength(String field, String value, int maxLength) {
        if (value.length() > maxLength) {
            throw badRequest(field + " cannot exceed " + maxLength + " characters");
        }
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private CompanyProfileResponse toResponse(
            CompanyProfile profile) {

        StoredFile logoStoredFile = profile.getLogoStoredFile();

        UUID responseLogoFileId = null;
        String responseLogoOriginalFilename = null;
        String responseLogoContentType = null;
        Long responseLogoSizeBytes = null;
        String responseLogoUrl = null;

        if (logoStoredFile != null) {
            responseLogoFileId = logoStoredFile.getStoredFileId();

            responseLogoOriginalFilename = logoStoredFile.getOriginalFileName();

            responseLogoContentType = logoStoredFile.getContentType();

            responseLogoSizeBytes = logoStoredFile.getSizeBytes();

            responseLogoUrl = LOGO_URL;
        }

        return CompanyProfileResponse.builder()
                .companyProfileId(profile.getCompanyProfileId())
                .profileCode(profile.getProfileCode())

                .companyName(profile.getCompanyName())
                .legalName(profile.getLegalName())
                .employerId(profile.getEmployerId())
                .country(profile.getCountry())

                .companyAddressLine1(profile.getCompanyAddressLine1())
                .companyAddressLine2(profile.getCompanyAddressLine2())
                .companyCity(profile.getCompanyCity())
                .companyState(profile.getCompanyState())
                .companyPostalCode(profile.getCompanyPostalCode())
                .companyCountry(profile.getCompanyCountry())

                .legalAddressLine1(profile.getLegalAddressLine1())
                .legalAddressLine2(profile.getLegalAddressLine2())
                .legalCity(profile.getLegalCity())
                .legalState(profile.getLegalState())
                .legalPostalCode(profile.getLegalPostalCode())
                .legalCountry(profile.getLegalCountry())

                .customerCommunicationAddressLine1(
                        profile.getCustomerCommunicationAddressLine1())
                .customerCommunicationAddressLine2(
                        profile.getCustomerCommunicationAddressLine2())
                .customerCommunicationCity(
                        profile.getCustomerCommunicationCity())
                .customerCommunicationState(
                        profile.getCustomerCommunicationState())
                .customerCommunicationPostalCode(
                        profile.getCustomerCommunicationPostalCode())
                .customerCommunicationCountry(
                        profile.getCustomerCommunicationCountry())

                .primaryPhone(profile.getPrimaryPhone())
                .email(profile.getEmail())
                .website(profile.getWebsite())
                .introductionData(profile.getIntroductionData())
                .defaultBidRoundingMode(
                        profile.getDefaultBidRoundingMode() != null
                                ? profile.getDefaultBidRoundingMode()
                                : BidRoundingMode.WHOLE)

                .logoFileId(responseLogoFileId)
                .logoOriginalFilename(responseLogoOriginalFilename)
                .logoContentType(responseLogoContentType)
                .logoSizeBytes(responseLogoSizeBytes)
                .logoUrl(responseLogoUrl)

                .syncToken(profile.getSyncToken())
                .isActive(profile.getIsActive())
                .createdAtUtc(profile.getCreatedAtUtc())
                .updatedAtUtc(profile.getUpdatedAtUtc())
                .build();
    }

    private void validateSyncToken(CompanyProfile profile, Integer syncToken) {
        if (syncToken == null) {
            throw badRequest("syncToken is required");
        }

        if (!syncToken.equals(profile.getSyncToken())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Company profile was updated by another user. Reload and try again.");
        }
    }

    private void validateLogoFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw badRequest("Logo file is required");
        }

        if (file.getSize() > MAX_LOGO_SIZE_BYTES) {
            throw badRequest("Logo file cannot exceed 5 MB");
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isBlank()) {
            throw badRequest("Logo file name is required");
        }

        String extension = getExtension(originalFilename);

        if (!ALLOWED_LOGO_EXTENSIONS.contains(extension)) {
            throw badRequest("Logo file type is not supported");
        }

        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_LOGO_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw badRequest("Logo content type is not supported");
        }
    }

    private String getExtension(String filename) {
        String safeFilename = Paths.get(filename).getFileName().toString();

        int dotIndex = safeFilename.lastIndexOf('.');

        if (dotIndex < 0 || dotIndex == safeFilename.length() - 1) {
            throw badRequest("Logo file extension is required");
        }

        return safeFilename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }

    private void deletePreviousReusableLogo(
            StoredFile previousStoredFile) {

        if (previousStoredFile == null) {
            return;
        }

        UUID storedFileId = previousStoredFile.getStoredFileId();

        /*
         * Confirm that Company Profile no longer references it.
         * Other unexpected typed references remain protected by database FKs.
         */
        if (companyProfileRepository
                .existsByLogoStoredFile_StoredFileId(storedFileId)) {

            throw new IllegalStateException(
                    "Previous company logo is still referenced");
        }

        storedFileService.deleteUnreferenced(previousStoredFile);
    }

    private MediaType resolveMediaType(
            String contentType) {

        if (contentType == null
                || contentType.isBlank()) {

            return MediaType.APPLICATION_OCTET_STREAM;
        }

        try {
            return MediaType.parseMediaType(contentType);
        } catch (RuntimeException exception) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    private String normalizeLogoDataUrlContentType(
            String contentType) {

        if (contentType == null
                || contentType.isBlank()) {
            return MediaType.IMAGE_PNG_VALUE;
        }

        try {
            return MediaType
                    .parseMediaType(contentType)
                    .toString();
        } catch (RuntimeException exception) {
            return MediaType.IMAGE_PNG_VALUE;
        }
    }

    private String normalizeLogoResponseFilename(
            String filename) {

        if (filename == null
                || filename.isBlank()) {
            return "company-logo";
        }

        return filename;
    }
}