package com.glassgang.pmworkflow.estimate.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BidOwnerResponse {

    private UUID id;
    private String username;
    private String displayName;
    private String role;
}