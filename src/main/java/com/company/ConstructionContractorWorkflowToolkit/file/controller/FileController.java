package com.company.ConstructionContractorWorkflowToolkit.file.controller;

import com.company.ConstructionContractorWorkflowToolkit.file.entity.SubstepFile;
import com.company.ConstructionContractorWorkflowToolkit.file.service.SubstepFileService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final SubstepFileService fileService;

    public FileController(SubstepFileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable UUID fileId) throws Exception {
        SubstepFile file = fileService.getFileEntity(fileId);

        Path path = Paths.get(file.getFileUrl());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/{fileId}/preview")
    public ResponseEntity<Resource> previewFile(@PathVariable UUID fileId) throws Exception {
        SubstepFile file = fileService.getFileEntity(fileId);

        Path path = Paths.get(file.getFileUrl());
        Resource resource = new UrlResource(path.toUri());

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;

        String name = file.getFileName().toLowerCase();

        if (name.endsWith(".pdf")) {
            mediaType = MediaType.APPLICATION_PDF;
        } else if (name.endsWith(".png")) {
            mediaType = MediaType.IMAGE_PNG;
        } else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            mediaType = MediaType.IMAGE_JPEG;
        } else if (name.endsWith(".txt")) {
            mediaType = MediaType.TEXT_PLAIN;
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + file.getFileName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{fileId}")
    public void deleteFile(@PathVariable UUID fileId) {
        fileService.deleteFile(fileId);
    }
}