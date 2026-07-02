package com.glassgang.pmworkflow.estimate.pdf;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/estimates/bids/revisions")
public class EstimatePdfHtmlPreviewController {

    private final EstimatePdfHtmlPreviewService estimatePdfHtmlPreviewService;

    public EstimatePdfHtmlPreviewController(EstimatePdfHtmlPreviewService estimatePdfHtmlPreviewService) {
        this.estimatePdfHtmlPreviewService = estimatePdfHtmlPreviewService;
    }

    @GetMapping(value = "/{bidRevisionId}/pdf-html-preview", produces = MediaType.TEXT_HTML_VALUE)
    public String getPdfHtmlPreview(@PathVariable UUID bidRevisionId) {
        return estimatePdfHtmlPreviewService.renderHtml(bidRevisionId);
    }
}