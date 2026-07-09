package com.glassgang.pmworkflow.estimate.dto.dictionary;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ConstructionObjectTypeResponse {

    private UUID constructionObjectTypeId;
    private String code;
    private String name;
    private String description;
    private Boolean isActive;
}