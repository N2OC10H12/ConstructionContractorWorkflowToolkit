package com.glassgang.pmworkflow.file.service;

import com.glassgang.pmworkflow.common.exception.BadRequestException;
import com.glassgang.pmworkflow.config.FileStorageProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private final FileStorageProperties properties;

    public FileStorageService(FileStorageProperties properties) {
        this.properties = properties;
    }

    public String store(UUID substepId, UUID fileId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required");
        }

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.isBlank()) {
            throw new BadRequestException("File name is required");
        }

        String safeFileName = Paths.get(originalFileName).getFileName().toString();

        Path substepFolder = Paths.get(properties.getRootPath(), substepId.toString());

        try {
            Files.createDirectories(substepFolder);

            Path targetPath = substepFolder.resolve(fileId + "_" + safeFileName);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return targetPath.toString();
        } catch (IOException e) {
            throw new BadRequestException("Failed to store file");
        }
    }
}