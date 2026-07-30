package com.company.ConstructionContractorWorkflowToolkit.estimate.service;

import com.company.ConstructionContractorWorkflowToolkit.audit.service.EstimateAuditService;
import com.company.ConstructionContractorWorkflowToolkit.common.exception.BadRequestException;
import com.company.ConstructionContractorWorkflowToolkit.common.exception.BusinessRuleException;
import com.company.ConstructionContractorWorkflowToolkit.common.exception.NotFoundException;
import com.company.ConstructionContractorWorkflowToolkit.common.util.CurrentUserUtil;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.BidRevisionItemQuoteResponse;
import com.company.ConstructionContractorWorkflowToolkit.estimate.entity.Bid;
import com.company.ConstructionContractorWorkflowToolkit.estimate.entity.BidRevision;
import com.company.ConstructionContractorWorkflowToolkit.estimate.entity.BidRevisionItem;
import com.company.ConstructionContractorWorkflowToolkit.estimate.entity.BidRevisionItemQuote;
import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.BidStatus;
import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.RevisionStatus;
import com.company.ConstructionContractorWorkflowToolkit.estimate.repository.BidRevisionItemQuoteRepository;
import com.company.ConstructionContractorWorkflowToolkit.estimate.repository.BidRevisionItemRepository;
import com.company.ConstructionContractorWorkflowToolkit.file.entity.StoredFile;
import com.company.ConstructionContractorWorkflowToolkit.file.service.StoredFileService;
import com.company.ConstructionContractorWorkflowToolkit.file.storage.StoredObjectContent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Collection;


@Service
public class BidRevisionItemQuoteService {

    private static final long MAX_QUOTE_SIZE_BYTES = 25L * 1024L * 1024L;

    private static final int MAX_DESCRIPTION_LENGTH = 500;

    /*
     * Without Apache Tika or another content-inspection dependency,
     * validation uses both the submitted extension and content type.
     *
     * application/octet-stream is accepted for known document extensions
     * because Windows clients sometimes submit generic content types.
     */
    private static final Map<String, Set<String>> ALLOWED_CONTENT_TYPES_BY_EXTENSION = Map.ofEntries(

            Map.entry(
                    "pdf",
                    Set.of(
                            "application/pdf",
                            "application/octet-stream")),

            Map.entry(
                    "png",
                    Set.of(
                            "image/png",
                            "application/octet-stream")),

            Map.entry(
                    "jpg",
                    Set.of(
                            "image/jpeg",
                            "image/jpg",
                            "application/octet-stream")),

            Map.entry(
                    "jpeg",
                    Set.of(
                            "image/jpeg",
                            "image/jpg",
                            "application/octet-stream")),

            Map.entry(
                    "webp",
                    Set.of(
                            "image/webp",
                            "application/octet-stream")),

            Map.entry(
                    "xls",
                    Set.of(
                            "application/vnd.ms-excel",
                            "application/octet-stream")),

            Map.entry(
                    "xlsx",
                    Set.of(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                            "application/octet-stream")),

            Map.entry(
                    "doc",
                    Set.of(
                            "application/msword",
                            "application/octet-stream")),

            Map.entry(
                    "docx",
                    Set.of(
                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                            "application/octet-stream")),

            Map.entry(
                    "csv",
                    Set.of(
                            "text/csv",
                            "application/csv",
                            "text/plain",
                            "application/vnd.ms-excel",
                            "application/octet-stream")));

    private static final String QUOTE_CONTENT_BASE_URL = "/api/estimates/bids/revisions/items/quotes/";

    private final BidRevisionItemRepository bidRevisionItemRepository;
    private final BidRevisionItemQuoteRepository quoteRepository;
    private final StoredFileService storedFileService;
    private final EstimateAccessService estimateAccessService;
    private final EstimateAuditService estimateAuditService;
    private final CurrentUserUtil currentUserUtil;

    public BidRevisionItemQuoteService(
            BidRevisionItemRepository bidRevisionItemRepository,
            BidRevisionItemQuoteRepository quoteRepository,
            StoredFileService storedFileService,
            EstimateAccessService estimateAccessService,
            EstimateAuditService estimateAuditService,
            CurrentUserUtil currentUserUtil) {

        this.bidRevisionItemRepository = bidRevisionItemRepository;

        this.quoteRepository = quoteRepository;

        this.storedFileService = storedFileService;

        this.estimateAccessService = estimateAccessService;

        this.estimateAuditService = estimateAuditService;

        this.currentUserUtil = currentUserUtil;
    }

    @Transactional
    public BidRevisionItemQuoteResponse uploadQuote(
            UUID bidRevisionItemId,
            MultipartFile file,
            String description) {

        BidRevisionItem item = getActiveItem(bidRevisionItemId);

        BidRevision revision = item.getBidRevision();

        Bid bid = revision.getBid();

        estimateAccessService.requireBidEditAccess(bid);
        requireQuoteMutationAllowed(bid, revision);

        validateQuoteFile(file);

        String normalizedDescription = normalizeDescription(description);

        UUID currentUserId = currentUserUtil.getCurrentUserId();

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        int nextDisplayOrder = quoteRepository
                .findTopDisplayOrderByBidRevisionItemId(
                        bidRevisionItemId)
                .orElse(0) + 1;

        UUID quoteId = UUID.randomUUID();

        UUID storedFileId = UUID.randomUUID();

        String objectKey = "estimate-item-quotes/"
                + bidRevisionItemId
                + "/"
                + storedFileId;

        StoredFile storedFile = storedFileService.store(
                storedFileId,
                objectKey,
                file);

        BidRevisionItemQuote quote = new BidRevisionItemQuote();

        quote.setBidRevisionItemQuoteId(quoteId);
        quote.setBidRevisionItem(item);
        quote.setStoredFile(storedFile);
        quote.setDescription(normalizedDescription);
        quote.setDisplayOrder(nextDisplayOrder);
        quote.setCreatedAtUtc(now);
        quote.setCreatedByUserId(currentUserId);

        BidRevisionItemQuote savedQuote = quoteRepository.saveAndFlush(quote);

        estimateAuditService.log(
                bid.getBidId(),
                revision.getBidRevisionId(),
                "UPLOADED",
                "BID_REVISION_ITEM_QUOTE",
                savedQuote.getBidRevisionItemQuoteId(),
                null,
                storedFile.getOriginalFileName(),
                "Quote uploaded for item: "
                        + item.getDescription());

        return toResponse(savedQuote);
    }

    @Transactional(readOnly = true)
    public List<BidRevisionItemQuoteResponse> getItemQuotes(
            UUID bidRevisionItemId) {

        BidRevisionItem item = getActiveItem(bidRevisionItemId);

        estimateAccessService.requireBidViewAccess(
                item.getBidRevision().getBid());

        return quoteRepository
                .findByBidRevisionItem_BidRevisionItemIdOrderByDisplayOrderAsc(
                        bidRevisionItemId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public QuoteContent loadQuoteContent(
            UUID bidRevisionItemQuoteId) {

        BidRevisionItemQuote quote = getQuote(bidRevisionItemQuoteId);

        BidRevisionItem item = quote.getBidRevisionItem();

        estimateAccessService.requireBidViewAccess(
                item.getBidRevision().getBid());

        StoredFile storedFile = quote.getStoredFile();

        StoredObjectContent content = storedFileService.loadContent(storedFile);

        return new QuoteContent(
                content,
                normalizeResponseFilename(
                        storedFile.getOriginalFileName()),
                storedFile.getContentType(),
                content.sizeBytes());
    }

    @Transactional
    public void deleteQuote(
            UUID bidRevisionItemQuoteId) {

        BidRevisionItemQuote quote = getQuote(bidRevisionItemQuoteId);

        BidRevisionItem item = quote.getBidRevisionItem();

        BidRevision revision = item.getBidRevision();

        Bid bid = revision.getBid();

        estimateAccessService.requireBidEditAccess(bid);
        requireQuoteMutationAllowed(bid, revision);

        StoredFile storedFile = quote.getStoredFile();

        UUID storedFileId = storedFile.getStoredFileId();

        String originalFileName = storedFile.getOriginalFileName();

        quoteRepository.delete(quote);
        quoteRepository.flush();

        long remainingQuoteReferences = quoteRepository.countByStoredFile_StoredFileId(
                storedFileId);

        if (remainingQuoteReferences == 0) {
            /*
             * Other attachment tables remain protected by their database
             * foreign keys. If an unexpected external reference exists,
             * StoredFile deletion fails and the entire transaction rolls
             * back without deleting physical content.
             */
            storedFileService.deleteUnreferenced(storedFile);
        }

        estimateAuditService.log(
                bid.getBidId(),
                revision.getBidRevisionId(),
                "DELETED",
                "BID_REVISION_ITEM_QUOTE",
                bidRevisionItemQuoteId,
                originalFileName,
                null,
                "Quote deleted from item: "
                        + item.getDescription());
    }

    @Transactional
    public void deleteQuotesForParentItems(
            Collection<UUID> bidRevisionItemIds) {

        if (bidRevisionItemIds == null
                || bidRevisionItemIds.isEmpty()) {
            return;
        }

        List<BidRevisionItemQuote> quotes = quoteRepository
                .findByBidRevisionItem_BidRevisionItemIdIn(
                        bidRevisionItemIds);

        if (quotes.isEmpty()) {
            return;
        }

        /*
         * Revision cloning allows multiple quote rows to share
         * one immutable StoredFile. Deduplicate stored files
         * before checking remaining references.
         */
        Map<UUID, StoredFile> storedFilesById = new LinkedHashMap<>();

        for (BidRevisionItemQuote quote : quotes) {

            StoredFile storedFile = quote.getStoredFile();

            storedFilesById.putIfAbsent(
                    storedFile.getStoredFileId(),
                    storedFile);
        }

        quoteRepository.deleteAll(quotes);
        quoteRepository.flush();

        for (StoredFile storedFile : storedFilesById.values()) {

            long remainingReferences = quoteRepository
                    .countByStoredFile_StoredFileId(
                            storedFile.getStoredFileId());

            if (remainingReferences == 0) {
                /*
                 * Foreign keys protect StoredFile from deletion
                 * if another typed attachment unexpectedly still
                 * references the same content.
                 */
                storedFileService.deleteUnreferenced(
                        storedFile);
            }
        }
    }

    private BidRevisionItem getActiveItem(
            UUID bidRevisionItemId) {

        if (bidRevisionItemId == null) {
            throw new BadRequestException(
                    "Bid revision item ID is required");
        }

        return bidRevisionItemRepository
                .findByBidRevisionItemIdAndIsDeletedFalse(
                        bidRevisionItemId)
                .orElseThrow(() -> new NotFoundException(
                        "Bid revision item not found"));
    }

    private BidRevisionItemQuote getQuote(
            UUID bidRevisionItemQuoteId) {

        if (bidRevisionItemQuoteId == null) {
            throw new BadRequestException(
                    "Quote ID is required");
        }

        return quoteRepository
                .findByBidRevisionItemQuoteIdAndBidRevisionItem_IsDeletedFalse(
                        bidRevisionItemQuoteId)
                .orElseThrow(() -> new NotFoundException(
                        "Bid revision item quote not found"));
    }

    private void requireQuoteMutationAllowed(
            Bid bid,
            BidRevision revision) {

        if (bid.getBidStatus() == BidStatus.AWARDED) {
            throw new BusinessRuleException(
                    "Awarded bid revision cannot be changed");
        }

        if (bid.getBidStatus() == BidStatus.LOST) {
            throw new BusinessRuleException(
                    "Lost bid revision cannot be changed");
        }

        if (bid.getBidStatus() == BidStatus.ARCHIVED) {
            throw new BusinessRuleException(
                    "Archived bid revision cannot be changed");
        }

        if (bid.getCurrentRevision() == null
                || !bid.getCurrentRevision()
                        .getBidRevisionId()
                        .equals(revision.getBidRevisionId())) {

            throw new BusinessRuleException(
                    "Only current revision can be changed");
        }

        if (revision.getRevisionStatus() != RevisionStatus.DRAFT) {

            throw new BusinessRuleException(
                    "Only DRAFT revisions can be changed");
        }
    }

    private void validateQuoteFile(
            MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException(
                    "Quote file is required");
        }

        if (file.getSize() > MAX_QUOTE_SIZE_BYTES) {
            throw new BadRequestException(
                    "Quote file cannot exceed 25 MB");
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null
                || originalFilename.isBlank()) {

            throw new BadRequestException(
                    "Quote file name is required");
        }

        String extension = getExtension(originalFilename);

        Set<String> allowedContentTypes = ALLOWED_CONTENT_TYPES_BY_EXTENSION
                .get(extension);

        if (allowedContentTypes == null) {
            throw new BadRequestException(
                    "Quote file type is not supported");
        }

        String contentType = file.getContentType();

        if (contentType == null
                || contentType.isBlank()) {

            throw new BadRequestException(
                    "Quote content type is required");
        }

        String normalizedContentType = contentType
                .trim()
                .toLowerCase(Locale.ROOT);

        if (!allowedContentTypes.contains(
                normalizedContentType)) {

            throw new BadRequestException(
                    "Quote content type does not match "
                            + "the file extension");
        }
    }

    private String getExtension(
            String filename) {

        String safeFilename = filename.replace('\\', '/');

        int finalSeparatorIndex = safeFilename.lastIndexOf('/');

        if (finalSeparatorIndex >= 0) {
            safeFilename = safeFilename.substring(
                    finalSeparatorIndex + 1);
        }

        int dotIndex = safeFilename.lastIndexOf('.');

        if (dotIndex < 0
                || dotIndex == safeFilename.length() - 1) {

            throw new BadRequestException(
                    "Quote file extension is required");
        }

        return safeFilename
                .substring(dotIndex + 1)
                .toLowerCase(Locale.ROOT);
    }

    private String normalizeDescription(
            String description) {

        if (description == null) {
            return null;
        }

        String normalized = description.trim();

        if (normalized.isEmpty()) {
            return null;
        }

        if (normalized.length() > MAX_DESCRIPTION_LENGTH) {

            throw new BadRequestException(
                    "Quote description cannot exceed "
                            + MAX_DESCRIPTION_LENGTH
                            + " characters");
        }

        return normalized;
    }

    private String normalizeResponseFilename(
            String filename) {

        if (filename == null
                || filename.isBlank()) {

            return "quote-file";
        }

        return filename;
    }

    private BidRevisionItemQuoteResponse toResponse(
            BidRevisionItemQuote quote) {

        StoredFile storedFile = quote.getStoredFile();

        UUID quoteId = quote.getBidRevisionItemQuoteId();

        return BidRevisionItemQuoteResponse.builder()
                .bidRevisionItemQuoteId(quoteId)
                .bidRevisionItemId(
                        quote.getBidRevisionItem()
                                .getBidRevisionItemId())

                .description(quote.getDescription())
                .displayOrder(quote.getDisplayOrder())

                .fileName(
                        storedFile.getOriginalFileName())
                .contentType(
                        storedFile.getContentType())
                .sizeBytes(
                        storedFile.getSizeBytes())

                .uploadedByUserId(
                        storedFile.getUploadedBy())
                .uploadedAtUtc(
                        storedFile.getUploadedAtUtc())

                .attachedByUserId(
                        quote.getCreatedByUserId())
                .attachedAtUtc(
                        quote.getCreatedAtUtc())

                .previewUrl(
                        QUOTE_CONTENT_BASE_URL
                                + quoteId
                                + "/preview")

                .downloadUrl(
                        QUOTE_CONTENT_BASE_URL
                                + quoteId
                                + "/download")

                .build();
    }

    public record QuoteContent(
            StoredObjectContent content,
            String filename,
            String contentType,
            long sizeBytes) {
    }
}