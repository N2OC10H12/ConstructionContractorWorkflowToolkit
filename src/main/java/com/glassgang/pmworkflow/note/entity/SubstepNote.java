package com.glassgang.pmworkflow.note.entity;

import com.glassgang.pmworkflow.project.entity.ProjectSubstep;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "substep_note")
public class SubstepNote {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "substep_id", nullable = false)
    private ProjectSubstep substep;

    @Column(name = "note_text", nullable = false)
    private String noteText;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}