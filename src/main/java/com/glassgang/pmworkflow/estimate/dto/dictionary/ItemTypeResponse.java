package com.glassgang.pmworkflow.estimate.dto.dictionary;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ItemTypeResponse {

    private UUID itemTypeId;
    private String code;
    private String name;
    private String description;
    private Boolean isActive;
}