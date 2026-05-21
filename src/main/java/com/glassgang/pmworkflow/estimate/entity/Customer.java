package com.glassgang.pmworkflow.estimate.entity;

import com.glassgang.pmworkflow.estimate.enums.CustomerType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "estimate", name = "customer")
public class Customer {

    @Id
    @Column(name = "customer_id")
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", nullable = false, length = 50)
    private CustomerType customerType;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "qbo_customer_id", length = 100)
    private String qboCustomerId;

    @Column(name = "internal_note")
    private String internalNote;

    @Column(name = "created_at_utc", nullable = false)
    private LocalDateTime createdAtUtc;

    @Column(name = "updated_at_utc", nullable = false)
    private LocalDateTime updatedAtUtc;

    @Column(name = "created_by_user_id")
    private UUID createdByUserId;

    @Column(name = "updated_by_user_id")
    private UUID updatedByUserId;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "deleted_at_utc")
    private LocalDateTime deletedAtUtc;

    @Column(name = "deleted_by_user_id")
    private UUID deletedByUserId;
}