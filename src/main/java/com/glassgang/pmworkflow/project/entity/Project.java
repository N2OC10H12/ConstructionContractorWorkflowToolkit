package com.glassgang.pmworkflow.project.entity;

import com.glassgang.pmworkflow.user.entity.AppUser;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "project")
public class Project {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private AppUser owner;

    @Column(name = "planning_deadline")
    private LocalDate planningDeadline;

    @Column(name = "project_deadline")
    private LocalDate projectDeadline;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private java.util.List<ProjectStep> steps;

    // getters/setters
}