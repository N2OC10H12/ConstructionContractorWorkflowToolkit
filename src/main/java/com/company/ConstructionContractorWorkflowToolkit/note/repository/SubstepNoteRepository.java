package com.company.ConstructionContractorWorkflowToolkit.note.repository;

import com.company.ConstructionContractorWorkflowToolkit.note.entity.SubstepNote;
import com.company.ConstructionContractorWorkflowToolkit.project.entity.ProjectSubstep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface SubstepNoteRepository extends JpaRepository<SubstepNote, UUID> {

    List<SubstepNote> findBySubstepOrderByCreatedAtAsc(ProjectSubstep substep);

    @Query("""
            select distinct n.substep.id
            from SubstepNote n
            where n.substep.id in :substepIds
            """)
    List<UUID> findSubstepIdsWithNotes(List<UUID> substepIds);
}