package com.glassgang.pmworkflow.estimate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "estimate", name = "tax_rate")
public class TaxRate {

    @Id
    @Column(name = "tax_rate_id")
    private UUID taxRateId;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "rate_percent", nullable = false, precision = 9, scale = 4)
    private BigDecimal ratePercent;

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

    @Column(name = "deleted_by_user_id")
    private UUID deletedByUserId;
}