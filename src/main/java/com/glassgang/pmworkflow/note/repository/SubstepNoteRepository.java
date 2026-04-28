package com.glassgang.pmworkflow.note.repository;

import com.glassgang.pmworkflow.note.entity.SubstepNote;
import com.glassgang.pmworkflow.project.entity.ProjectSubstep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubstepNoteRepository extends JpaRepository<SubstepNote, UUID> {

    List<SubstepNote> findBySubstepOrderByCreatedAtAsc(ProjectSubstep substep);
}