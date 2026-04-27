package com.glassgang.pmworkflow.project.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "project_step")
public class ProjectStep {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "template_step_id")
    private UUID templateStepId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Column(name = "deadline")
    private LocalDate deadline;

    @Column(name = "assigned_user_id")
    private UUID assignedUserId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "step", fetch = FetchType.LAZY)
    private java.util.List<ProjectSubstep> substeps;

    // getters/setters
}