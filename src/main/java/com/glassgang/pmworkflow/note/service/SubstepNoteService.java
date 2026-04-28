package com.glassgang.pmworkflow.note.service;

import com.glassgang.pmworkflow.common.exception.BadRequestException;
import com.glassgang.pmworkflow.common.exception.NotFoundException;
import com.glassgang.pmworkflow.common.util.CurrentUserUtil;
import com.glassgang.pmworkflow.note.dto.CreateSubstepNoteRequest;
import com.glassgang.pmworkflow.note.dto.SubstepNoteResponse;
import com.glassgang.pmworkflow.note.entity.SubstepNote;
import com.glassgang.pmworkflow.note.repository.SubstepNoteRepository;
import com.glassgang.pmworkflow.project.entity.ProjectSubstep;
import com.glassgang.pmworkflow.project.repository.ProjectSubstepRepository;
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

    public SubstepNoteService(SubstepNoteRepository noteRepository,
                              ProjectSubstepRepository substepRepository,
                              CurrentUserUtil currentUserUtil) {
        this.noteRepository = noteRepository;
        this.substepRepository = substepRepository;
        this.currentUserUtil = currentUserUtil;
    }

    @Transactional
    public SubstepNoteResponse createNote(UUID substepId, CreateSubstepNoteRequest request) {
        if (request.getNoteText() == null || request.getNoteText().isBlank()) {
            throw new BadRequestException("Note text is required");
        }

        ProjectSubstep substep = substepRepository.findById(substepId)
                .orElseThrow(() -> new NotFoundException("Substep not found"));

        SubstepNote note = new SubstepNote();
        note.setId(UUID.randomUUID());
        note.setSubstep(substep);
        note.setNoteText(request.getNoteText().trim());

        note.setCreatedBy(currentUserUtil.getCurrentUserId());

        note.setCreatedAt(LocalDateTime.now());

        return toResponse(noteRepository.save(note));
    }

    @Transactional(readOnly = true)
    public List<SubstepNoteResponse> getNotes(UUID substepId) {
        ProjectSubstep substep = substepRepository.findById(substepId)
                .orElseThrow(() -> new NotFoundException("Substep not found"));

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