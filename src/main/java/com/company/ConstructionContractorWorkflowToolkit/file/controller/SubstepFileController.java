package com.company.ConstructionContractorWorkflowToolkit.file.controller;

import com.company.ConstructionContractorWorkflowToolkit.file.dto.DeleteSubstepFilesRequest;
import com.company.ConstructionContractorWorkflowToolkit.file.dto.SubstepFileResponse;
import com.company.ConstructionContractorWorkflowToolkit.file.service.SubstepFileService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/substeps/{substepId}/files")
public class SubstepFileController {

    private final SubstepFileService fileService;

    public SubstepFileController(SubstepFileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SubstepFileResponse uploadFile(
            @PathVariable UUID substepId,
            @RequestPart("file") MultipartFile file
    ) {
        return fileService.uploadFile(substepId, file);
    }

    @PostMapping(value = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<SubstepFileResponse> uploadFiles(
            @PathVariable UUID substepId,
            @RequestPart("files") List<MultipartFile> files
    ) {
        return fileService.uploadFiles(substepId, files);
    }

    @GetMapping
    public List<SubstepFileResponse> getFiles(@PathVariable UUID substepId) {
        return fileService.getFiles(substepId);
    }

    @DeleteMapping
    public void deleteFiles(
            @PathVariable UUID substepId,
            @Valid @RequestBody DeleteSubstepFilesRequest request
    ) {
        fileService.deleteFiles(substepId, request.getFileIds());
    }
}