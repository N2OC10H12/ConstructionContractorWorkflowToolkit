package com.company.ConstructionContractorWorkflowToolkit.file.repository;

import com.company.ConstructionContractorWorkflowToolkit.file.entity.SubstepFile;
import com.company.ConstructionContractorWorkflowToolkit.project.entity.ProjectSubstep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface SubstepFileRepository extends JpaRepository<SubstepFile, UUID> {

    List<SubstepFile> findBySubstepOrderByUploadedAtAsc(ProjectSubstep substep);

    @Query("""
            select distinct f.substep.id
            from SubstepFile f
            where f.substep.id in :substepIds
            """)
    List<UUID> findSubstepIdsWithFiles(List<UUID> substepIds);
}