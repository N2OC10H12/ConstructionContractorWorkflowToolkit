package com.company.ConstructionContractorWorkflowToolkit.file.service;

import com.company.ConstructionContractorWorkflowToolkit.audit.service.ProjectAuditService;
import com.company.ConstructionContractorWorkflowToolkit.common.exception.BadRequestException;
import com.company.ConstructionContractorWorkflowToolkit.common.exception.NotFoundException;
import com.company.ConstructionContractorWorkflowToolkit.file.dto.SubstepFileResponse;
import com.company.ConstructionContractorWorkflowToolkit.file.entity.StoredFile;
import com.company.ConstructionContractorWorkflowToolkit.file.entity.SubstepFile;
import com.company.ConstructionContractorWorkflowToolkit.file.repository.SubstepFileRepository;
import com.company.ConstructionContractorWorkflowToolkit.file.storage.StoredObjectContent;
import com.company.ConstructionContractorWorkflowToolkit.project.entity.ProjectSubstep;
import com.company.ConstructionContractorWorkflowToolkit.project.repository.ProjectSubstepRepository;
import com.company.ConstructionContractorWorkflowToolkit.project.service.ProjectAccessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class SubstepFileService {

    private static final String OBJECT_KEY_PREFIX =
            "project-substeps";

    private final SubstepFileRepository fileRepository;
    private final ProjectSubstepRepository substepRepository;
    private final StoredFileService storedFileService;
    private final ProjectAccessService projectAccessService;
    private final ProjectAuditService auditService;

    public SubstepFileService(
            SubstepFileRepository fileRepository,
            ProjectSubstepRepository substepRepository,
            StoredFileService storedFileService,
            ProjectAccessService projectAccessService,
            ProjectAuditService auditService
    ) {
        this.fileRepository = fileRepository;
        this.substepRepository = substepRepository;
        this.storedFileService = storedFileService;
        this.projectAccessService = projectAccessService;
        this.auditService = auditService;
    }

    @Transactional
    public SubstepFileResponse uploadFile(
            UUID substepId,
            MultipartFile file
    ) {
        ProjectSubstep substep =
                requireSubstep(substepId);

        /*
         * Authorization must happen before physical content is written.
         */
        projectAccessService.requireProjectEditAccess(
                substep.getStep().getProject()
        );

        return storeAttachment(substep, file);
    }

    @Transactional
    public List<SubstepFileResponse> uploadFiles(
            UUID substepId,
            List<MultipartFile> files
    ) {
        if (files == null || files.isEmpty()) {
            throw new BadRequestException(
                    "At least one file is required"
            );
        }

        ProjectSubstep substep =
                requireSubstep(substepId);

        /*
         * Authorize once before writing the first physical object.
         */
        projectAccessService.requireProjectEditAccess(
                substep.getStep().getProject()
        );

        /*
         * All files participate in the same transaction. If any upload,
         * attachment insert, or audit operation fails, StoredFileService's
         * rollback callbacks remove every physical object written during
         * this batch.
         */
        return files.stream()
                .map(file -> storeAttachment(substep, file))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SubstepFileResponse> getFiles(UUID substepId) {
        ProjectSubstep substep =
                requireSubstep(substepId);

        /*
         * Preserve existing behavior. Listing currently requires edit
         * access rather than view access.
         */
        projectAccessService.requireProjectEditAccess(
                substep.getStep().getProject()
        );

        return fileRepository
                .findBySubstepOrderByCreatedAtUtcAsc(substep)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Loads an attachment through its Project authorization boundary.
     *
     * The returned StoredObjectContent owns an open InputStream. The HTTP
     * controller must pass it to Spring as a Resource so the stream is
     * closed after the response is written.
     */
    @Transactional(readOnly = true)
    public SubstepFileContent openFile(UUID fileId) {
        SubstepFile file =
                requireFileEntity(fileId);

        projectAccessService.requireProjectViewAccess(
                file.getSubstep()
                        .getStep()
                        .getProject()
        );

        StoredFile storedFile =
                file.getStoredFile();

        StoredObjectContent content =
                storedFileService.loadContent(storedFile);

        return new SubstepFileContent(
                storedFile.getOriginalFileName(),
                storedFile.getContentType(),
                content
        );
    }

    @Transactional
    public void deleteFile(UUID fileId) {
        SubstepFile file =
                requireFileEntity(fileId);

        projectAccessService.requireProjectEditAccess(
                file.getSubstep()
                        .getStep()
                        .getProject()
        );

        StoredFile storedFile =
                file.getStoredFile();

        UUID projectId =
                file.getSubstep()
                        .getStep()
                        .getProject()
                        .getId();

        String fileName =
                storedFile.getOriginalFileName();

        auditService.log(
                projectId,
                "FILE_DELETED",
                "FILE",
                file.getId(),
                "name=" + fileName,
                null
        );

        /*
         * Remove and flush the typed attachment before checking whether
         * its StoredFile remains referenced.
         */
        fileRepository.delete(file);
        fileRepository.flush();

        deleteStoredFileWhenUnreferenced(storedFile);
    }

    @Transactional
    public void deleteFiles(
            UUID substepId,
            List<UUID> fileIds
    ) {
        if (fileIds == null || fileIds.isEmpty()) {
            throw new BadRequestException(
                    "At least one file id is required"
            );
        }

        Set<UUID> uniqueFileIds =
                new LinkedHashSet<>(fileIds);

        if (uniqueFileIds.size() != fileIds.size()) {
            throw new BadRequestException(
                    "Duplicate file ids are not allowed"
            );
        }

        ProjectSubstep substep =
                requireSubstep(substepId);

        projectAccessService.requireProjectEditAccess(
                substep.getStep().getProject()
        );

        List<SubstepFile> files =
                fileRepository.findAllById(uniqueFileIds);

        if (files.size() != uniqueFileIds.size()) {
            throw new NotFoundException(
                    "One or more files not found"
            );
        }

        for (SubstepFile file : files) {
            if (!file.getSubstep()
                    .getId()
                    .equals(substepId)) {

                throw new BadRequestException(
                        "One or more files do not belong to this substep"
                );
            }
        }

        Map<UUID, StoredFile> storedFilesById =
                new LinkedHashMap<>();

        for (SubstepFile file : files) {
            StoredFile storedFile =
                    file.getStoredFile();

            storedFilesById.put(
                    storedFile.getStoredFileId(),
                    storedFile
            );

            auditService.log(
                    substep.getStep()
                            .getProject()
                            .getId(),
                    "FILE_DELETED",
                    "FILE",
                    file.getId(),
                    "name=" +
                            storedFile.getOriginalFileName(),
                    null
            );
        }

        /*
         * Delete every attachment relationship first. Only after the flush
         * can reference counts accurately determine which immutable stored
         * objects are now unreferenced.
         */
        fileRepository.deleteAll(files);
        fileRepository.flush();

        for (StoredFile storedFile :
                storedFilesById.values()) {

            deleteStoredFileWhenUnreferenced(storedFile);
        }
    }

    private SubstepFileResponse storeAttachment(
            ProjectSubstep substep,
            MultipartFile file
    ) {
        validateUploadFile(file);

        UUID attachmentId =
                UUID.randomUUID();

        UUID storedFileId =
                UUID.randomUUID();

        String objectKey =
                OBJECT_KEY_PREFIX
                        + "/"
                        + substep.getId()
                        + "/"
                        + storedFileId;

        StoredFile storedFile =
                storedFileService.store(
                        storedFileId,
                        objectKey,
                        file
                );

        SubstepFile attachment =
                new SubstepFile();

        attachment.setId(attachmentId);
        attachment.setSubstep(substep);
        attachment.setStoredFile(storedFile);
        attachment.setCreatedAtUtc(
                storedFile.getUploadedAtUtc()
        );

        SubstepFile savedAttachment =
                fileRepository.saveAndFlush(attachment);

        auditService.log(
                substep.getStep()
                        .getProject()
                        .getId(),
                "FILE_UPLOADED",
                "FILE",
                savedAttachment.getId(),
                null,
                "name=" +
                        storedFile.getOriginalFileName()
        );

        return toResponse(savedAttachment);
    }

    private void validateUploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException(
                    "File is required"
            );
        }

        String originalFileName =
                file.getOriginalFilename();

        if (originalFileName == null
                || originalFileName.isBlank()) {

            throw new BadRequestException(
                    "File name is required"
            );
        }
    }

    private ProjectSubstep requireSubstep(
            UUID substepId
    ) {
        if (substepId == null) {
            throw new BadRequestException(
                    "Substep id is required"
            );
        }

        return substepRepository.findById(substepId)
                .orElseThrow(
                        () -> new NotFoundException(
                                "Substep not found"
                        )
                );
    }

    private SubstepFile requireFileEntity(
            UUID fileId
    ) {
        if (fileId == null) {
            throw new BadRequestException(
                    "File id is required"
            );
        }

        return fileRepository.findById(fileId)
                .orElseThrow(
                        () -> new NotFoundException(
                                "File not found"
                        )
                );
    }

    private void deleteStoredFileWhenUnreferenced(
            StoredFile storedFile
    ) {
        long remainingReferences =
                fileRepository
                        .countByStoredFile_StoredFileId(
                                storedFile.getStoredFileId()
                        );

        if (remainingReferences == 0) {
            storedFileService.deleteUnreferenced(
                    storedFile
            );
        }
    }

    private SubstepFileResponse toResponse(
            SubstepFile file
    ) {
        StoredFile storedFile =
                file.getStoredFile();

        SubstepFileResponse response =
                new SubstepFileResponse();

        response.setId(file.getId());
        response.setSubstepId(
                file.getSubstep().getId()
        );

        response.setFileName(
                storedFile.getOriginalFileName()
        );

        response.setUploadedBy(
                storedFile.getUploadedBy()
        );

        response.setUploadedAt(
                storedFile.getUploadedAtUtc()
        );

        response.setPreviewUrl(
                "/api/files/"
                        + file.getId()
                        + "/preview"
        );

        response.setDownloadUrl(
                "/api/files/"
                        + file.getId()
                        + "/download"
        );

        return response;
    }

    public record SubstepFileContent(
            String fileName,
            String contentType,
            StoredObjectContent content
    ) {
    }
}