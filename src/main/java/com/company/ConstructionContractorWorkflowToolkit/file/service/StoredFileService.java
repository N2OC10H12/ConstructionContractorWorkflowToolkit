package com.company.ConstructionContractorWorkflowToolkit.file.service;

import com.company.ConstructionContractorWorkflowToolkit.common.util.CurrentUserUtil;
import com.company.ConstructionContractorWorkflowToolkit.config.FileStorageProperties;
import com.company.ConstructionContractorWorkflowToolkit.file.entity.StoredFile;
import com.company.ConstructionContractorWorkflowToolkit.file.repository.StoredFileRepository;
import com.company.ConstructionContractorWorkflowToolkit.file.storage.ObjectStorageException;
import com.company.ConstructionContractorWorkflowToolkit.file.storage.ObjectStorageProvider;
import com.company.ConstructionContractorWorkflowToolkit.file.storage.ObjectStorageProviderRegistry;
import com.company.ConstructionContractorWorkflowToolkit.file.storage.StorageObjectReference;
import com.company.ConstructionContractorWorkflowToolkit.file.storage.StorageProviderType;
import com.company.ConstructionContractorWorkflowToolkit.file.storage.StoredObjectContent;
import com.company.ConstructionContractorWorkflowToolkit.file.storage.StoredObjectResult;
import com.company.ConstructionContractorWorkflowToolkit.file.storage.StoredObjectWriteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class StoredFileService {

    private static final Logger log = LoggerFactory.getLogger(StoredFileService.class);

    private static final int STORAGE_CONTAINER_MAX_LENGTH = 255;
    private static final int STORAGE_OBJECT_KEY_MAX_LENGTH = 1024;
    private static final int PROVIDER_OBJECT_ID_MAX_LENGTH = 512;
    private static final int PROVIDER_VERSION_TAG_MAX_LENGTH = 512;
    private static final int ORIGINAL_FILE_NAME_MAX_LENGTH = 255;
    private static final int CONTENT_TYPE_MAX_LENGTH = 255;

    private static final Pattern UNSAFE_DISPLAY_CHARACTERS = Pattern.compile("[\\p{Cntrl}\\p{Cf}]");

    private static final Pattern SHA_256_PATTERN = Pattern.compile("^[0-9a-fA-F]{64}$");

    private final StoredFileRepository storedFileRepository;
    private final ObjectStorageProviderRegistry providerRegistry;
    private final FileStorageProperties fileStorageProperties;
    private final CurrentUserUtil currentUserUtil;

    public StoredFileService(
            StoredFileRepository storedFileRepository,
            ObjectStorageProviderRegistry providerRegistry,
            FileStorageProperties fileStorageProperties,
            CurrentUserUtil currentUserUtil) {
        this.storedFileRepository = storedFileRepository;
        this.providerRegistry = providerRegistry;
        this.fileStorageProperties = fileStorageProperties;
        this.currentUserUtil = currentUserUtil;
    }

    /**
     * Stores immutable physical content and persists its generic metadata.
     *
     * Domain services remain responsible for:
     * - loading and authorizing the owning domain object;
     * - validating feature-specific file rules;
     * - generating the stored-file ID and logical object key;
     * - creating the typed attachment record;
     * - writing domain audit records.
     *
     * This method joins an existing domain transaction when one exists.
     * If it starts its own transaction, the physical object and StoredFile
     * metadata are committed together.
     */
    @Transactional
    public StoredFile store(
            UUID storedFileId,
            String objectKey,
            MultipartFile file) {
        UUID validatedStoredFileId = requireStoredFileId(storedFileId);

        String validatedObjectKey = normalizeObjectKey(objectKey);

        MultipartFile validatedFile = requireFile(file);

        /*
         * Resolve authentication before writing physical content.
         * An unauthenticated request must not leave a provider object behind.
         */
        UUID uploadedBy = currentUserUtil.getCurrentUserId();

        StorageProviderType providerType = requireConfiguredProviderType();

        ObjectStorageProvider provider = providerRegistry.requireProvider(providerType);

        String originalFileName = normalizeOriginalFileName(
                validatedFile.getOriginalFilename());

        String contentType = normalizeContentType(
                validatedFile.getContentType());

        long expectedSizeBytes = validatedFile.getSize();

        StorageObjectReference cleanupReference = new StorageObjectReference(
                null,
                validatedObjectKey,
                null);

        boolean physicalObjectStored = false;
        boolean rollbackCleanupRegistered = false;

        try (InputStream inputStream = validatedFile.getInputStream()) {

            StoredObjectWriteRequest writeRequest = new StoredObjectWriteRequest(
                    null,
                    validatedObjectKey,
                    inputStream,
                    expectedSizeBytes,
                    contentType);

            StoredObjectResult result = provider.store(writeRequest);

            physicalObjectStored = true;

            cleanupReference = createCleanupReference(
                    result,
                    validatedObjectKey);

            StoredFile storedFile = createStoredFileEntity(
                    validatedStoredFileId,
                    providerType,
                    result,
                    originalFileName,
                    contentType,
                    uploadedBy);

            /*
             * Register this before persisting metadata. It remains attached
             * to the outer domain transaction when this method participates
             * in one.
             */
            registerRollbackCleanup(
                    provider,
                    cleanupReference,
                    validatedStoredFileId);

            rollbackCleanupRegistered = true;

            /*
             * Flush now so database constraints are checked before the
             * service returns. A later failure in the outer transaction still
             * triggers the registered physical-object cleanup.
             */
            return storedFileRepository.saveAndFlush(storedFile);

        } catch (IOException exception) {
            ObjectStorageException storageException = new ObjectStorageException(
                    "Unable to read uploaded file content",
                    exception);

            cleanupImmediatelyWhenRequired(
                    provider,
                    cleanupReference,
                    physicalObjectStored,
                    rollbackCleanupRegistered,
                    storageException);

            throw storageException;

        } catch (RuntimeException exception) {
            cleanupImmediatelyWhenRequired(
                    provider,
                    cleanupReference,
                    physicalObjectStored,
                    rollbackCleanupRegistered,
                    exception);

            throw exception;
        }
    }

    /**
     * Loads physical content using the provider recorded on the StoredFile
     * row. The caller must authorize access through the typed domain
     * attachment before invoking this method.
     */
    public StoredObjectContent loadContent(StoredFile storedFile) {
        StoredFile validatedStoredFile = requireStoredFileMetadata(storedFile);

        ObjectStorageProvider provider = providerRegistry.requireProvider(
                validatedStoredFile.getStorageProvider());

        return provider.load(
                createStorageReference(validatedStoredFile));
    }

    /**
     * Deletes unreferenced StoredFile metadata in the current transaction
     * and removes its physical provider object only after the database
     * transaction commits successfully.
     *
     * The calling domain service must first:
     * - authorize the domain operation;
     * - delete or detach the typed attachment;
     * - verify that no typed attachment still references this StoredFile.
     *
     * This method intentionally does not perform domain-reference checks
     * because StoredFileService must not depend on Project, Estimate, or
     * Company Profile attachment tables.
     */
    @Transactional
    public void deleteUnreferenced(StoredFile storedFile) {
        StoredFile validatedStoredFile = requireStoredFileMetadata(storedFile);

        requireActiveTransactionForDeletion();

        ObjectStorageProvider provider = providerRegistry.requireProvider(
                validatedStoredFile.getStorageProvider());

        StorageObjectReference reference = createStorageReference(validatedStoredFile);

        registerAfterCommitDeletion(
                provider,
                reference,
                validatedStoredFile.getStoredFileId());

        /*
         * Flush now so an unexpected remaining foreign-key reference is
         * detected before this method returns. If the flush or a later domain
         * operation fails, the transaction rolls back and afterCommit is not
         * called, so the physical object remains intact.
         */
        storedFileRepository.delete(validatedStoredFile);
        storedFileRepository.flush();
    }

    private StoredFile createStoredFileEntity(
            UUID storedFileId,
            StorageProviderType providerType,
            StoredObjectResult result,
            String originalFileName,
            String contentType,
            UUID uploadedBy) {
        if (result == null) {
            throw new ObjectStorageException(
                    "Storage provider returned no result");
        }

        String resultObjectKey = requireProviderObjectKey(result.objectKey());

        String resultContainer = validateNullableProviderValue(
                result.container(),
                STORAGE_CONTAINER_MAX_LENGTH,
                "Storage container");

        String providerObjectId = validateNullableProviderValue(
                result.providerObjectId(),
                PROVIDER_OBJECT_ID_MAX_LENGTH,
                "Provider object ID");

        String providerVersionTag = validateNullableProviderValue(
                result.providerVersionTag(),
                PROVIDER_VERSION_TAG_MAX_LENGTH,
                "Provider version tag");

        if (result.sizeBytes() < 0) {
            throw new ObjectStorageException(
                    "Storage provider returned a negative object size");
        }

        String sha256 = normalizeSha256(result.sha256());

        StoredFile storedFile = new StoredFile();

        storedFile.setStoredFileId(storedFileId);
        storedFile.setStorageProvider(providerType);
        storedFile.setStorageContainer(resultContainer);
        storedFile.setStorageObjectKey(resultObjectKey);
        storedFile.setProviderObjectId(providerObjectId);
        storedFile.setProviderVersionTag(providerVersionTag);
        storedFile.setOriginalFileName(originalFileName);
        storedFile.setContentType(contentType);
        storedFile.setSizeBytes(result.sizeBytes());
        storedFile.setSha256(sha256);
        storedFile.setUploadedBy(uploadedBy);

        /*
         * The database column is explicitly uploaded_at_utc.
         * Use UTC rather than the operating system's default timezone.
         */
        storedFile.setUploadedAtUtc(
                LocalDateTime.now(ZoneOffset.UTC));

        return storedFile;
    }

    private void registerRollbackCleanup(
            ObjectStorageProvider provider,
            StorageObjectReference reference,
            UUID storedFileId) {
        if (!TransactionSynchronizationManager
                .isSynchronizationActive()
                || !TransactionSynchronizationManager
                        .isActualTransactionActive()) {

            throw new ObjectStorageException(
                    "An active database transaction is required " +
                            "when storing file content");
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCompletion(int status) {
                        if (status == STATUS_ROLLED_BACK) {
                            deleteAfterRollback(
                                    provider,
                                    reference,
                                    storedFileId);
                            return;
                        }

                        if (status == STATUS_UNKNOWN) {
                            /*
                             * Do not automatically delete when the commit
                             * result is unknown. The metadata may have
                             * committed, and deleting here could create a
                             * broken database reference.
                             */
                            log.error(
                                    "Transaction completion status is unknown " +
                                            "for stored file {}. Physical " +
                                            "object was retained for " +
                                            "reconciliation. Provider={}, " +
                                            "container={}, objectKey={}",
                                    storedFileId,
                                    provider.getType(),
                                    reference.container(),
                                    reference.objectKey());
                        }
                    }
                });
    }

    private void deleteAfterRollback(
            ObjectStorageProvider provider,
            StorageObjectReference reference,
            UUID storedFileId) {
        try {
            provider.delete(reference);
        } catch (RuntimeException cleanupException) {
            /*
             * afterCompletion runs after the database transaction has already
             * finished. There is nothing useful to roll back at this point,
             * so record enough information for later reconciliation.
             */
            log.error(
                    "Unable to remove physical object after transaction " +
                            "rollback. StoredFileId={}, provider={}, " +
                            "container={}, objectKey={}",
                    storedFileId,
                    provider.getType(),
                    reference.container(),
                    reference.objectKey(),
                    cleanupException);
        }
    }

    private void cleanupImmediatelyWhenRequired(
            ObjectStorageProvider provider,
            StorageObjectReference reference,
            boolean physicalObjectStored,
            boolean rollbackCleanupRegistered,
            RuntimeException originalException) {
        if (!physicalObjectStored || rollbackCleanupRegistered) {
            return;
        }

        try {
            provider.delete(reference);
        } catch (RuntimeException cleanupException) {
            originalException.addSuppressed(cleanupException);

            log.error(
                    "Unable to remove physical object after file-storage " +
                            "metadata failure. Provider={}, container={}, " +
                            "objectKey={}",
                    provider.getType(),
                    reference.container(),
                    reference.objectKey(),
                    cleanupException);
        }
    }

    private StorageObjectReference createCleanupReference(
            StoredObjectResult result,
            String requestedObjectKey) {
        if (result == null) {
            return new StorageObjectReference(
                    null,
                    requestedObjectKey,
                    null);
        }

        String resultObjectKey = isBlank(result.objectKey())
                ? requestedObjectKey
                : result.objectKey();

        return new StorageObjectReference(
                result.container(),
                resultObjectKey,
                result.providerObjectId());
    }

    private UUID requireStoredFileId(UUID storedFileId) {
        if (storedFileId == null) {
            throw new IllegalArgumentException(
                    "Stored file ID is required");
        }

        return storedFileId;
    }

    private MultipartFile requireFile(MultipartFile file) {
        if (file == null) {
            throw new IllegalArgumentException(
                    "Uploaded file is required");
        }

        if (file.getSize() < 0) {
            throw new IllegalArgumentException(
                    "Uploaded file size cannot be negative");
        }

        return file;
    }

    private StorageProviderType requireConfiguredProviderType() {
        StorageProviderType providerType = fileStorageProperties.getProvider();

        if (providerType == null) {
            throw new ObjectStorageException(
                    "Default storage provider is not configured");
        }

        return providerType;
    }

    private String normalizeObjectKey(String objectKey) {
        if (objectKey == null) {
            throw new IllegalArgumentException(
                    "Storage object key is required");
        }

        String normalized = objectKey.strip();

        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(
                    "Storage object key is required");
        }

        if (normalized.length() > STORAGE_OBJECT_KEY_MAX_LENGTH) {

            throw new IllegalArgumentException(
                    "Storage object key cannot exceed " +
                            STORAGE_OBJECT_KEY_MAX_LENGTH +
                            " characters");
        }

        return normalized;
    }

    private String requirePersistedObjectKey(String objectKey) {
        if (isBlank(objectKey)) {
            throw new ObjectStorageException(
                    "Stored file has no storage object key");
        }

        if (objectKey.length() > STORAGE_OBJECT_KEY_MAX_LENGTH) {

            throw new ObjectStorageException(
                    "Stored file object key exceeds the supported length");
        }

        return objectKey;
    }

    private String requireProviderObjectKey(String objectKey) {
        if (isBlank(objectKey)) {
            throw new ObjectStorageException(
                    "Storage provider returned no object key");
        }

        if (objectKey.length() > STORAGE_OBJECT_KEY_MAX_LENGTH) {

            throw new ObjectStorageException(
                    "Storage provider object key cannot exceed " +
                            STORAGE_OBJECT_KEY_MAX_LENGTH +
                            " characters");
        }

        return objectKey;
    }

    private String normalizeOriginalFileName(
            String originalFileName) {
        String normalized = originalFileName == null
                ? ""
                : Normalizer.normalize(
                        originalFileName,
                        Normalizer.Form.NFC);

        /*
         * Browsers and clients may submit either a Unix path or a Windows
         * fake path. Preserve only the final display filename.
         */
        normalized = normalized.replace('\\', '/');

        int finalSeparatorIndex = normalized.lastIndexOf('/');

        if (finalSeparatorIndex >= 0) {
            normalized = normalized.substring(
                    finalSeparatorIndex + 1);
        }

        normalized = UNSAFE_DISPLAY_CHARACTERS
                .matcher(normalized)
                .replaceAll("")
                .strip();

        if (normalized.isEmpty()
                || ".".equals(normalized)
                || "..".equals(normalized)) {

            normalized = "file";
        }

        return truncateFileName(normalized);
    }

    private String truncateFileName(String fileName) {
        if (fileName.length() <= ORIGINAL_FILE_NAME_MAX_LENGTH) {

            return fileName;
        }

        int extensionIndex = fileName.lastIndexOf('.');

        if (extensionIndex > 0) {
            String extension = fileName.substring(extensionIndex);

            /*
             * Preserve a normal-sized extension when truncating the display
             * name. Unusually long suffixes are treated as part of the name.
             */
            if (extension.length() <= 32) {
                int baseLength = ORIGINAL_FILE_NAME_MAX_LENGTH
                        - extension.length();

                return fileName.substring(0, baseLength)
                        + extension;
            }
        }

        return fileName.substring(
                0,
                ORIGINAL_FILE_NAME_MAX_LENGTH);
    }

    private String normalizeContentType(String contentType) {
        if (contentType == null) {
            return null;
        }

        String normalized = UNSAFE_DISPLAY_CHARACTERS
                .matcher(contentType)
                .replaceAll("")
                .strip();

        if (normalized.isEmpty()) {
            return null;
        }

        if (normalized.length() > CONTENT_TYPE_MAX_LENGTH) {
            return normalized.substring(
                    0,
                    CONTENT_TYPE_MAX_LENGTH);
        }

        return normalized;
    }

    private String normalizeSha256(String sha256) {
        if (isBlank(sha256)) {
            return null;
        }

        String normalized = sha256
                .strip()
                .toLowerCase(Locale.ROOT);

        if (!SHA_256_PATTERN
                .matcher(normalized)
                .matches()) {

            throw new ObjectStorageException(
                    "Storage provider returned an invalid SHA-256 value");
        }

        return normalized;
    }

    private String validateNullableProviderValue(
            String value,
            int maxLength,
            String fieldName) {
        if (isBlank(value)) {
            return null;
        }

        if (value.length() > maxLength) {
            throw new ObjectStorageException(
                    fieldName +
                            " cannot exceed " +
                            maxLength +
                            " characters");
        }

        return value;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private StoredFile requireStoredFileMetadata(
            StoredFile storedFile) {
        if (storedFile == null) {
            throw new IllegalArgumentException(
                    "Stored file metadata is required");
        }

        if (storedFile.getStoredFileId() == null) {
            throw new ObjectStorageException(
                    "Stored file metadata has no ID");
        }

        if (storedFile.getStorageProvider() == null) {
            throw new ObjectStorageException(
                    "Stored file has no storage provider");
        }

        requirePersistedObjectKey(
                storedFile.getStorageObjectKey());

        return storedFile;
    }

    private StorageObjectReference createStorageReference(
            StoredFile storedFile) {
        return new StorageObjectReference(
                storedFile.getStorageContainer(),
                requirePersistedObjectKey(
                        storedFile.getStorageObjectKey()),
                storedFile.getProviderObjectId());
    }

    private void requireActiveTransactionForDeletion() {
        if (!TransactionSynchronizationManager
                .isSynchronizationActive()
                || !TransactionSynchronizationManager
                        .isActualTransactionActive()) {

            throw new ObjectStorageException(
                    "An active database transaction is required " +
                            "when deleting stored file content");
        }
    }

    private void registerAfterCommitDeletion(
            ObjectStorageProvider provider,
            StorageObjectReference reference,
            UUID storedFileId) {
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {
                        try {
                            provider.delete(reference);
                        } catch (RuntimeException cleanupException) {
                            /*
                             * The database transaction has already committed.
                             * Keep a detailed log so the physical orphan can be
                             * reconciled or retried later.
                             */
                            log.error(
                                    "Unable to remove physical object after " +
                                            "StoredFile metadata deletion committed. " +
                                            "StoredFileId={}, provider={}, " +
                                            "container={}, objectKey={}",
                                    storedFileId,
                                    provider.getType(),
                                    reference.container(),
                                    reference.objectKey(),
                                    cleanupException);
                        }
                    }

                    @Override
                    public void afterCompletion(int status) {
                        if (status == STATUS_UNKNOWN) {
                            /*
                             * Do not delete when the database completion result
                             * is uncertain. The StoredFile row may still exist.
                             */
                            log.error(
                                    "Transaction completion status is unknown " +
                                            "while deleting stored file {}. " +
                                            "Physical object was retained. " +
                                            "Provider={}, container={}, objectKey={}",
                                    storedFileId,
                                    provider.getType(),
                                    reference.container(),
                                    reference.objectKey());
                        }
                    }
                });
    }
}