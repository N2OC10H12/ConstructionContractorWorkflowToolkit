package com.company.ConstructionContractorWorkflowToolkit.file.controller;

import com.company.ConstructionContractorWorkflowToolkit.file.service.SubstepFileService;
import com.company.ConstructionContractorWorkflowToolkit.file.service.SubstepFileService.SubstepFileContent;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final SubstepFileService fileService;

    public FileController(
            SubstepFileService fileService
    ) {
        this.fileService = fileService;
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable UUID fileId
    ) {
        SubstepFileContent file =
                fileService.openFile(fileId);

        Resource resource =
                new InputStreamResource(
                        file.content().inputStream()
                );

        String contentDisposition =
                ContentDisposition.attachment()
                        .filename(
                                file.fileName(),
                                StandardCharsets.UTF_8
                        )
                        .build()
                        .toString();

        return ResponseEntity.ok()
                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM
                )
                .contentLength(
                        file.content().sizeBytes()
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        contentDisposition
                )
                .body(resource);
    }

    @GetMapping("/{fileId}/preview")
    public ResponseEntity<Resource> previewFile(
            @PathVariable UUID fileId
    ) {
        SubstepFileContent file =
                fileService.openFile(fileId);

        Resource resource =
                new InputStreamResource(
                        file.content().inputStream()
                );

        String contentDisposition =
                ContentDisposition.inline()
                        .filename(
                                file.fileName(),
                                StandardCharsets.UTF_8
                        )
                        .build()
                        .toString();

        return ResponseEntity.ok()
                .contentType(
                        resolveMediaType(file.contentType())
                )
                .contentLength(
                        file.content().sizeBytes()
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        contentDisposition
                )
                .body(resource);
    }

    @DeleteMapping("/{fileId}")
    public void deleteFile(
            @PathVariable UUID fileId
    ) {
        fileService.deleteFile(fileId);
    }

    private MediaType resolveMediaType(
            String contentType
    ) {
        if (contentType == null
                || contentType.isBlank()) {

            return MediaType.APPLICATION_OCTET_STREAM;
        }

        try {
            return MediaType.parseMediaType(
                    contentType
            );
        } catch (IllegalArgumentException exception) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}