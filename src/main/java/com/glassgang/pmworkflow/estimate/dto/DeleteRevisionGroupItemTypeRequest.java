package com.glassgang.pmworkflow.estimate.dto;

import jakarta.validation.constraints.*;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteRevisionGroupItemTypeRequest {
    @NotBlank
    @Size(max = 255)
    private String groupName;

    @NotNull
    private UUID itemTypeId;
}
