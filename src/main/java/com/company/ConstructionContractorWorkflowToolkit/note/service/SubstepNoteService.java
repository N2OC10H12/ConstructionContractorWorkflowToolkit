package com.company.ConstructionContractorWorkflowToolkit.note.service;

import com.company.ConstructionContractorWorkflowToolkit.audit.service.ProjectAuditService;
import com.company.ConstructionContractorWorkflowToolkit.common.exception.BadRequestException;
import com.company.ConstructionContractorWorkflowToolkit.common.exception.NotFoundException;
import com.company.ConstructionContractorWorkflowToolkit.common.util.CurrentUserUtil;
import com.company.ConstructionContractorWorkflowToolkit.note.dto.CreateSubstepNoteRequest;
import com.company.ConstructionContractorWorkflowToolkit.note.dto.SubstepNoteResponse;
import com.company.ConstructionContractorWorkflowToolkit.note.entity.SubstepNote;
import com.company.ConstructionContractorWorkflowToolkit.note.repository.SubstepNoteRepository;
import com.company.ConstructionContractorWorkflowToolkit.project.entity.ProjectSubstep;
import com.company.ConstructionContractorWorkflowToolkit.project.repository.ProjectSubstepRepository;
import com.company.ConstructionContractorWorkflowToolkit.project.service.ProjectAccessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SubstepNoteService {

    private final SubstepNoteRepository noteRepository;
    private final ProjectSubstepRepository substepRepository;
    private final CurrentUserUtil currentUserUtil;
    private final ProjectAccessService projectAccessService;
    private final ProjectAuditService auditService;

    public SubstepNoteService(SubstepNoteRepository noteRepository,
                              ProjectSubstepRepository substepRepository,
                              CurrentUserUtil currentUserUtil,
                              ProjectAccessService projectAccessService,
                              ProjectAuditService auditService) {
        this.noteRepository = noteRepository;
        this.substepRepository = substepRepository;
        this.currentUserUtil = currentUserUtil;
        this.projectAccessService = projectAccessService;
        this.auditService = auditService;
    }

    @Transactional
    public SubstepNoteResponse createNote(UUID substepId, CreateSubstepNoteRequest request) {
        if (request.getNoteText() == null || request.getNoteText().isBlank()) {
            throw new BadRequestException("Note text is required");
        }

        ProjectSubstep substep = substepRepository.findById(substepId)
                .orElseThrow(() -> new NotFoundException("Substep not found"));

        projectAccessService.requireProjectEditAccess(
                substep.getStep().getProject()
        );

        SubstepNote note = new SubstepNote();
        note.setId(UUID.randomUUID());
        note.setSubstep(substep);
        note.setNoteText(request.getNoteText().trim());

        note.setCreatedBy(currentUserUtil.getCurrentUserId());

        note.setCreatedAt(LocalDateTime.now());

        SubstepNote savedNote = noteRepository.save(note);

        auditService.log(
                substep.getStep().getProject().getId(),
                "NOTE_CREATED",
                "NOTE",
                note.getId(),
                null,
                "text=" + note.getNoteText()
        );

        return toResponse(savedNote);
    }

    @Transactional(readOnly = true)
    public List<SubstepNoteResponse> getNotes(UUID substepId) {
        ProjectSubstep substep = substepRepository.findById(substepId)
                .orElseThrow(() -> new NotFoundException("Substep not found"));

        projectAccessService.requireProjectViewAccess(
                substep.getStep().getProject()
        );

        return noteRepository.findBySubstepOrderByCreatedAtAsc(substep).stream()
                .map(this::toResponse)
                .toList();
    }

    private SubstepNoteResponse toResponse(SubstepNote note) {
        SubstepNoteResponse response = new SubstepNoteResponse();
        response.setId(note.getId());
        response.setSubstepId(note.getSubstep().getId());
        response.setNoteText(note.getNoteText());
        response.setCreatedBy(note.getCreatedBy());
        response.setCreatedAt(note.getCreatedAt());

        return response;
    }
}