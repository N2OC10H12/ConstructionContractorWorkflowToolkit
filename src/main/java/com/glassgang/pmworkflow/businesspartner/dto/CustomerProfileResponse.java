package com.glassgang.pmworkflow.businesspartner.dto;

import com.glassgang.pmworkflow.businesspartner.enums.CustomerCategory;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CustomerProfileResponse {
    private UUID customerProfileId;
    private UUID businessPartnerId;

    private CustomerCategory customerCategory;

    private Boolean defaultTaxable;
    private String resaleNumber;
    private String internalNote;

    private LocalDateTime createdAtUtc;
    private LocalDateTime updatedAtUtc;
}