package com.glassgang.pmworkflow.estimate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(schema = "estimate", name = "estimate_pdf_template")
public class EstimatePdfTemplate {

    @Id
    @Column(name = "estimate_pdf_template_id")
    private UUID estimatePdfTemplateId;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "html_template", nullable = false)
    private String htmlTemplate;

    @Column(name = "css_template")
    private String cssTemplate;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "created_at_utc", nullable = false)
    private LocalDateTime createdAtUtc;

    @Column(name = "updated_at_utc")
    private LocalDateTime updatedAtUtc;

    @Column(name = "deleted_at_utc")
    private LocalDateTime deletedAtUtc;

    @Column(name = "updated_by_user_id")
    private UUID updatedByUserId;

    @Column(name = "deleted_by_user_id")
    private UUID deletedByUserId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "template_definition_json", columnDefinition = "jsonb")
    private String templateDefinitionJson;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;
}