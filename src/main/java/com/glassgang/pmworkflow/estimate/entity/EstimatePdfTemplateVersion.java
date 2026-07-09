package com.glassgang.pmworkflow.estimate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "estimate", name = "estimate_pdf_template_version")
public class EstimatePdfTemplateVersion {

    @Id
    @Column(name = "estimate_pdf_template_version_id", nullable = false)
    private UUID estimatePdfTemplateVersionId;

    @Column(name = "estimate_pdf_template_id", nullable = false)
    private UUID estimatePdfTemplateId;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "html_template", nullable = false, columnDefinition = "text")
    private String htmlTemplate;

    @Column(name = "css_template", columnDefinition = "text")
    private String cssTemplate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "template_definition_json", columnDefinition = "jsonb")
    private String templateDefinitionJson;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at_utc", nullable = false)
    private LocalDateTime createdAtUtc;

    @Column(name = "created_by_user_id")
    private UUID createdByUserId;

    @Column(name = "change_note", columnDefinition = "text")
    private String changeNote;
}