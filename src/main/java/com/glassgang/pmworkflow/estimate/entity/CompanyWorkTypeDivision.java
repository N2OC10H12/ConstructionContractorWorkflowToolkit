package com.glassgang.pmworkflow.estimate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        schema = "estimate",
        name = "company_work_type_division",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_company_work_type_division_code",
                        columnNames = "division_code")
        })
public class CompanyWorkTypeDivision {

    @Id
    @Column(name = "company_work_type_division_id")
    private UUID companyWorkTypeDivisionId;

    @Column(name = "division_code", nullable = false, length = 2)
    private String divisionCode;

    @Column(name = "division_name", nullable = false, length = 200)
    private String divisionName;

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled;

    @Column(name = "enabled_at_utc")
    private LocalDateTime enabledAtUtc;

    @Column(name = "enabled_by_user_id")
    private UUID enabledByUserId;

    @Column(name = "created_at_utc", nullable = false)
    private LocalDateTime createdAtUtc;

    @Column(name = "updated_at_utc")
    private LocalDateTime updatedAtUtc;
}