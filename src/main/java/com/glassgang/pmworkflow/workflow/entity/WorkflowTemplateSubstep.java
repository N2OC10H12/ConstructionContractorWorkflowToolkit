package com.glassgang.pmworkflow.workflow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "workflow_template_substep")
public class WorkflowTemplateSubstep {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "step_id", nullable = false)
    private WorkflowTemplateStep step;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;
}