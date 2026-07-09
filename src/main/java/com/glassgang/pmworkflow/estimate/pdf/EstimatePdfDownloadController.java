package com.glassgang.pmworkflow.estimate.pdf;

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

    public EstimatePdfDownloadController(
            EstimatePdfGenerationService estimatePdfGenerationService) {
        this.estimatePdfGenerationService = estimatePdfGenerationService;
    }

    @GetMapping(value = "/{bidRevisionId}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadPdf(@PathVariable UUID bidRevisionId) {
        byte[] pdfBytes = estimatePdfGenerationService.generatePdf(bidRevisionId);

        String filename = "estimate-" + bidRevisionId + ".pdf";

        return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
        .header(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment()
                        .filename(filename, StandardCharsets.UTF_8)
                        .build()
                        .toString())
        .body(pdfBytes);
    }
}