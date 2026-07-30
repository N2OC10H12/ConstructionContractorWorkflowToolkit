package com.company.ConstructionContractorWorkflowToolkit.config;

import com.company.ConstructionContractorWorkflowToolkit.file.storage.StorageProviderType;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.nio.file.Path;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "app.file-storage")
public class FileStorageProperties {

    @NotNull
    private StorageProviderType provider = StorageProviderType.LOCAL;

    @NotNull
    private Path rootPath = Path.of("./data/uploads");
}