package com.company.ConstructionContractorWorkflowToolkit.estimate.controller;

import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.BidRevisionItemQuoteResponse;
import com.company.ConstructionContractorWorkflowToolkit.estimate.service.BidRevisionItemQuoteService;
import com.company.ConstructionContractorWorkflowToolkit.estimate.service.BidRevisionItemQuoteService.QuoteContent;
import com.company.ConstructionContractorWorkflowToolkit.file.storage.StoredObjectContent;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/estimates/bids/revisions/items")
@RequiredArgsConstructor
public class BidRevisionItemQuoteController {

    private final BidRevisionItemQuoteService quoteService;

    @PostMapping(value = "/{bidRevisionItemId}/quotes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public BidRevisionItemQuoteResponse uploadQuote(
            @PathVariable UUID bidRevisionItemId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {

        return quoteService.uploadQuote(
                bidRevisionItemId,
                file,
                description);
    }

    @GetMapping("/{bidRevisionItemId}/quotes")
    public List<BidRevisionItemQuoteResponse> getItemQuotes(
            @PathVariable UUID bidRevisionItemId) {

        return quoteService.getItemQuotes(
                bidRevisionItemId);
    }

    @GetMapping("/quotes/{bidRevisionItemQuoteId}/preview")
    public ResponseEntity<Resource> previewQuote(
            @PathVariable UUID bidRevisionItemQuoteId) {

        QuoteContent quoteContent = quoteService.loadQuoteContent(
                bidRevisionItemQuoteId);

        return buildContentResponse(
                quoteContent,
                true);
    }

    @GetMapping("/quotes/{bidRevisionItemQuoteId}/download")
    public ResponseEntity<Resource> downloadQuote(
            @PathVariable UUID bidRevisionItemQuoteId) {

        QuoteContent quoteContent = quoteService.loadQuoteContent(
                bidRevisionItemQuoteId);

        return buildContentResponse(
                quoteContent,
                false);
    }

    @DeleteMapping("/quotes/{bidRevisionItemQuoteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuote(
            @PathVariable UUID bidRevisionItemQuoteId) {

        quoteService.deleteQuote(
                bidRevisionItemQuoteId);
    }

    private ResponseEntity<Resource> buildContentResponse(
            QuoteContent quoteContent,
            boolean preview) {

        StoredObjectContent storedContent = quoteContent.content();

        ContentDisposition contentDisposition = preview
                ? ContentDisposition.inline()
                        .filename(
                                quoteContent.filename(),
                                StandardCharsets.UTF_8)
                        .build()
                : ContentDisposition.attachment()
                        .filename(
                                quoteContent.filename(),
                                StandardCharsets.UTF_8)
                        .build();

        MediaType responseMediaType = preview
                ? resolveMediaType(
                        quoteContent.contentType())
                : MediaType.APPLICATION_OCTET_STREAM;

        InputStreamResource resource = new InputStreamResource(
                storedContent.inputStream());

        return ResponseEntity.ok()
                .contentType(responseMediaType)
                .contentLength(
                        storedContent.sizeBytes())
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        contentDisposition.toString())
                .body(resource);
    }

    private MediaType resolveMediaType(
            String contentType) {

        if (contentType == null
                || contentType.isBlank()) {

            return MediaType.APPLICATION_OCTET_STREAM;
        }

        try {
            return MediaType.parseMediaType(
                    contentType);
        } catch (RuntimeException exception) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}