package com.company.ConstructionContractorWorkflowToolkit.project.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "project_substep")
public class ProjectSubstep {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "step_id", nullable = false)
    private ProjectStep step;

    @Column(name = "template_substep_id")
    private UUID templateSubstepId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Column(name = "is_done", nullable = false)
    private Boolean isDone;

    @Column(name = "assigned_user_id")
    private UUID assignedUserId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // getters/setters
}