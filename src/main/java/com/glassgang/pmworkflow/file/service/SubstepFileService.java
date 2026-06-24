package com.glassgang.pmworkflow.file.service;

import com.glassgang.pmworkflow.audit.service.ProjectAuditService;
import com.glassgang.pmworkflow.common.exception.BadRequestException;
import com.glassgang.pmworkflow.project.service.ProjectAccessService;
import com.glassgang.pmworkflow.common.exception.NotFoundException;
import com.glassgang.pmworkflow.common.util.CurrentUserUtil;
import com.glassgang.pmworkflow.file.dto.SubstepFileResponse;
import com.glassgang.pmworkflow.file.entity.SubstepFile;
import com.glassgang.pmworkflow.file.repository.SubstepFileRepository;
import com.glassgang.pmworkflow.project.entity.ProjectSubstep;
import com.glassgang.pmworkflow.project.repository.ProjectSubstepRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class SubstepFileService {

    private final SubstepFileRepository fileRepository;
    private final ProjectSubstepRepository substepRepository;
    private final FileStorageService fileStorageService;
    private final CurrentUserUtil currentUserUtil;
    private final ProjectAccessService projectAccessService;
    private final ProjectAuditService auditService;

    public SubstepFileService(SubstepFileRepository fileRepository,
                              ProjectSubstepRepository substepRepository,
                              FileStorageService fileStorageService,
                              CurrentUserUtil currentUserUtil,
                              ProjectAccessService projectAccessService,
                              ProjectAuditService auditService) {
        this.fileRepository = fileRepository;
        this.substepRepository = substepRepository;
        this.fileStorageService = fileStorageService;
        this.currentUserUtil = currentUserUtil;
        this.projectAccessService = projectAccessService;
        this.auditService = auditService;
    }

    @Transactional
    public SubstepFileResponse uploadFile(UUID substepId, MultipartFile file) {
        ProjectSubstep substep = substepRepository.findById(substepId)
                .orElseThrow(() -> new NotFoundException("Substep not found"));

        UUID fileId = UUID.randomUUID();

        String storedPath = fileStorageService.store(substepId, fileId, file);

        projectAccessService.requireProjectEditAccess(
                substep.getStep().getProject()
        );

        SubstepFile substepFile = new SubstepFile();
        substepFile.setId(fileId);
        substepFile.setSubstep(substep);
        substepFile.setFileName(file.getOriginalFilename());
        substepFile.setFileUrl(storedPath);

        substepFile.setUploadedBy(currentUserUtil.getCurrentUserId());

        substepFile.setUploadedAt(LocalDateTime.now());

        SubstepFile savedFile = fileRepository.save(substepFile);

        auditService.log(
                substep.getStep().getProject().getId(),
                "FILE_UPLOADED",
                "FILE",
                savedFile.getId(),
                null,
                "name=" + savedFile.getFileName()
        );

        return toResponse(savedFile);
    }

    @Transactional(readOnly = true)
    public List<SubstepFileResponse> getFiles(UUID substepId) {
        ProjectSubstep substep = substepRepository.findById(substepId)
                .orElseThrow(() -> new NotFoundException("Substep not found"));

        projectAccessService.requireProjectEditAccess(
                substep.getStep().getProject()
        );

        return fileRepository.findBySubstepOrderByUploadedAtAsc(substep).stream()
                .map(this::toResponse)
                .toList();
    }

    private SubstepFileResponse toResponse(SubstepFile file) {
        SubstepFileResponse response = new SubstepFileResponse();
        response.setId(file.getId());
        response.setSubstepId(file.getSubstep().getId());
        response.setFileName(file.getFileName());
        response.setPreviewUrl("/api/files/" + file.getId() + "/preview");
        response.setDownloadUrl("/api/files/" + file.getId() + "/download");
        response.setUploadedBy(file.getUploadedBy());
        response.setUploadedAt(file.getUploadedAt());
        return response;
    }

    @Transactional
    public List<SubstepFileResponse> uploadFiles(UUID substepId, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new BadRequestException("At least one file is required");
        }

        return files.stream()
                .map(file -> uploadFile(substepId, file))
                .toList();
    }

    @Transactional(readOnly = true)
    public SubstepFile getFileEntity(UUID fileId) {
        SubstepFile file = fileRepository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("File not found"));

        projectAccessService.requireProjectViewAccess(
                file.getSubstep().getStep().getProject()
        );

        return file;
    }

    @Transactional
    public void deleteFile(UUID fileId) {
        SubstepFile file = fileRepository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("File not found"));

        projectAccessService.requireProjectEditAccess(
                file.getSubstep().getStep().getProject()
        );

        Path path = Paths.get(file.getFileUrl());

        try {
            Files.deleteIfExists(path);
        } catch (Exception e) {
            throw new BadRequestException("Failed to delete physical file");
        }

        auditService.log(
                file.getSubstep().getStep().getProject().getId(),
                "FILE_DELETED",
                "FILE",
                file.getId(),
                "name=" + file.getFileName(),
                null
        );

        fileRepository.delete(file);
    }

    @Transactional
    public void deleteFiles(UUID substepId, List<UUID> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            throw new BadRequestException("At least one file id is required");
        }

        ProjectSubstep substep = substepRepository.findById(substepId)
                .orElseThrow(() -> new NotFoundException("Substep not found"));

        projectAccessService.requireProjectEditAccess(
                substep.getStep().getProject()
        );

        List<SubstepFile> files = fileRepository.findAllById(fileIds);

        if (files.size() != fileIds.size()) {
            throw new NotFoundException("One or more files not found");
        }

        for (SubstepFile file : files) {
            if (!file.getSubstep().getId().equals(substepId)) {
                throw new BadRequestException("One or more files do not belong to this substep");
            }
        }

        for (SubstepFile file : files) {
            Path path = Paths.get(file.getFileUrl());

            try {
                Files.deleteIfExists(path);
            } catch (Exception e) {
                throw new BadRequestException("Failed to delete physical file");
            }
        }

        for (SubstepFile file : files) {
            auditService.log(
                    file.getSubstep().getStep().getProject().getId(),
                    "FILE_DELETED",
                    "FILE",
                    file.getId(),
                    "name=" + file.getFileName(),
                    null
            );
        }

        fileRepository.deleteAll(files);
    }
}