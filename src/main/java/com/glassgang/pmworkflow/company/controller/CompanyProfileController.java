package com.glassgang.pmworkflow.company.controller;

import com.glassgang.pmworkflow.company.dto.CompanyProfileResponse;
import com.glassgang.pmworkflow.company.dto.UpdateCompanyProfileRequest;
import com.glassgang.pmworkflow.company.service.CompanyProfileService;
import com.glassgang.pmworkflow.company.service.CompanyProfileService.CompanyLogoResource;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/company/profile")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CompanyProfileController {

    private final CompanyProfileService companyProfileService;

    @GetMapping
    public CompanyProfileResponse getDefaultProfile() {
        return companyProfileService.getDefaultProfile();
    }

    @PatchMapping
    public CompanyProfileResponse updateDefaultProfile(@RequestBody UpdateCompanyProfileRequest request) {
        return companyProfileService.updateDefaultProfile(request);
    }

    @PostMapping(value = "/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CompanyProfileResponse uploadLogo(
            @RequestPart("file") MultipartFile file,
            @RequestParam("syncToken") Integer syncToken
    ) {
        return companyProfileService.uploadLogo(file, syncToken);
    }

    @GetMapping("/logo")
    public ResponseEntity<Resource> getLogo() {
        CompanyLogoResource logo = companyProfileService.getLogoResource();

        return ResponseEntity.ok()
                .contentType(logo.mediaType())
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline()
                                .filename(logo.filename())
                                .build()
                                .toString()
                )
                .body(logo.resource());
    }

    @DeleteMapping("/logo")
    public CompanyProfileResponse deleteLogo(@RequestParam("syncToken") Integer syncToken) {
        return companyProfileService.deleteLogo(syncToken);
    }
}