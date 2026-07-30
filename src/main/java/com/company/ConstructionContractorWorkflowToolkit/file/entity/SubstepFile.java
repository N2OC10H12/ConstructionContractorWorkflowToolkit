package com.company.ConstructionContractorWorkflowToolkit.file.entity;

import com.company.ConstructionContractorWorkflowToolkit.project.entity.ProjectSubstep;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "substep_file")
public class SubstepFile {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "substep_id", nullable = false)
    private ProjectSubstep substep;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stored_file_id", nullable = false)
    private StoredFile storedFile;

    @Column(name = "created_at_utc", nullable = false)
    private LocalDateTime createdAtUtc;
}