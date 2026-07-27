package com.company.ConstructionContractorWorkflowToolkit.note.controller;

import com.company.ConstructionContractorWorkflowToolkit.note.dto.CreateSubstepNoteRequest;
import com.company.ConstructionContractorWorkflowToolkit.note.dto.SubstepNoteResponse;
import com.company.ConstructionContractorWorkflowToolkit.note.service.SubstepNoteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/substeps/{substepId}/notes")
public class SubstepNoteController {

    private final SubstepNoteService noteService;

    public SubstepNoteController(SubstepNoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    public SubstepNoteResponse createNote(
            @PathVariable UUID substepId,
            @RequestBody CreateSubstepNoteRequest request
    ) {
        return noteService.createNote(substepId, request);
    }

    @GetMapping
    public List<SubstepNoteResponse> getNotes(@PathVariable UUID substepId) {
        return noteService.getNotes(substepId);
    }
}