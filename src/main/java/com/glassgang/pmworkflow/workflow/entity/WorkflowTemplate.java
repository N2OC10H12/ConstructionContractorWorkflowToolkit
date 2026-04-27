package com.glassgang.pmworkflow.workflow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "workflow_template")
public class WorkflowTemplate {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;

    @OneToMany(mappedBy = "template", fetch = FetchType.LAZY)
    private List<WorkflowTemplateStep> steps;
}