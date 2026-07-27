package com.company.ConstructionContractorWorkflowToolkit.estimate.pdf;

import com.company.ConstructionContractorWorkflowToolkit.estimate.service.BidService;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/api/estimates/bids/revisions")
public class EstimatePdfDownloadController {

    private final EstimatePdfGenerationService estimatePdfGenerationService;
    private final BidService bidService;

    public EstimatePdfDownloadController(
            EstimatePdfGenerationService estimatePdfGenerationService,
            BidService bidService) {
        this.estimatePdfGenerationService = estimatePdfGenerationService;
        this.bidService = bidService;
    }

    @GetMapping(value = "/{bidRevisionId}/pdf-preview", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> previewPdf(
            @PathVariable UUID bidRevisionId) {

        bidService.requireBidRevisionPdfPreviewAccess(bidRevisionId);

        byte[] pdfBytes = estimatePdfGenerationService
                .generateSavedTemplatePreviewPdf(bidRevisionId);

        String filename = "estimate-preview-" + bidRevisionId + ".pdf";

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_PDF_VALUE)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline()
                                .filename(filename, StandardCharsets.UTF_8)
                                .build()
                                .toString())
                .cacheControl(CacheControl.noStore())
                .body(pdfBytes);
    }

    @GetMapping(value = "/{bidRevisionId}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadPdf(
            @PathVariable UUID bidRevisionId) {

        bidService.requireBidRevisionPdfDownloadAccess(bidRevisionId);

        byte[] pdfBytes = estimatePdfGenerationService.generatePdf(bidRevisionId);

        String filename = "estimate-" + bidRevisionId + ".pdf";

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_PDF_VALUE)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(filename, StandardCharsets.UTF_8)
                                .build()
                                .toString())
                .cacheControl(CacheControl.noStore())
                .body(pdfBytes);
    }
}