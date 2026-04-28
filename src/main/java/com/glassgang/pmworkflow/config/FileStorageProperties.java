package com.glassgang.pmworkflow.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "pmworkflow.file-storage")
public class FileStorageProperties {

    private String rootPath;
}