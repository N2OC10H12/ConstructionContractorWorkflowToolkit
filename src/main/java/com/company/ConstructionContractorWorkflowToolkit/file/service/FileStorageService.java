package com.company.ConstructionContractorWorkflowToolkit.file.service;

import com.company.ConstructionContractorWorkflowToolkit.common.exception.BadRequestException;
import com.company.ConstructionContractorWorkflowToolkit.config.FileStorageProperties;
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

        Path substepFolder = properties.getRootPath()
                .resolve(substepId.toString());

        try {
            Files.createDirectories(substepFolder);

            Path targetPath = substepFolder.resolve(fileId + "_" + safeFileName);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return targetPath.toString();
        } catch (IOException e) {
            throw new BadRequestException("Failed to store file");
        }
    }

    public String storeInFolder(String relativeFolder, UUID fileId, MultipartFile file, String extension) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required");
        }

        if (relativeFolder == null || relativeFolder.isBlank()) {
            throw new BadRequestException("Storage folder is required");
        }

        if (extension == null || extension.isBlank()) {
            throw new BadRequestException("File extension is required");
        }

        String cleanExtension = extension.startsWith(".")
                ? extension.substring(1)
                : extension;

        Path folder = properties.getRootPath()
                .resolve(relativeFolder);

        try {
            Files.createDirectories(folder);

            Path targetPath = folder.resolve(fileId + "." + cleanExtension);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return targetPath.toString();
        } catch (IOException e) {
            throw new BadRequestException("Failed to store file");
        }
    }

    public void deleteByPath(String storagePath) {
        if (storagePath == null || storagePath.isBlank()) {
            return;
        }

        try {
            Files.deleteIfExists(Paths.get(storagePath));
        } catch (IOException e) {
            throw new BadRequestException("Failed to delete physical file");
        }
    }
}