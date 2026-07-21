package com.glassgang.pmworkflow.estimate.pdf;

import com.glassgang.pmworkflow.estimate.dto.pdf.EstimatePdfDesignerRegistryResponse;
import com.glassgang.pmworkflow.estimate.dto.pdf.EstimatePdfTemplateResponse;
import com.glassgang.pmworkflow.estimate.dto.pdf.PreviewEstimatePdfTemplateRequest;
import com.glassgang.pmworkflow.estimate.dto.pdf.UpdateEstimatePdfTemplateRequest;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/estimates/pdf-templates")
public class EstimatePdfTemplateController {

    private final EstimatePdfTemplateService estimatePdfTemplateService;
    private final EstimatePdfTemplateRenderService estimatePdfTemplateRenderService;
    private final EstimatePdfTemplateAccessService estimatePdfTemplateAccessService;
    private final EstimatePdfTemplateDesignerRegistryService designerRegistryService;
    private final EstimatePdfGenerationService estimatePdfGenerationService;

    public EstimatePdfTemplateController(
            EstimatePdfTemplateService estimatePdfTemplateService,
            EstimatePdfTemplateRenderService estimatePdfTemplateRenderService,
            EstimatePdfTemplateAccessService estimatePdfTemplateAccessService,
            EstimatePdfTemplateDesignerRegistryService designerRegistryService,
            EstimatePdfGenerationService estimatePdfGenerationService) {
        this.estimatePdfTemplateService = estimatePdfTemplateService;
        this.estimatePdfTemplateRenderService = estimatePdfTemplateRenderService;
        this.estimatePdfTemplateAccessService = estimatePdfTemplateAccessService;
        this.designerRegistryService = designerRegistryService;
        this.estimatePdfGenerationService = estimatePdfGenerationService;
    }

    @GetMapping("/default")
    public EstimatePdfTemplateResponse getDefaultTemplate() {
        return estimatePdfTemplateService.getDefaultTemplate();
    }

    @PatchMapping("/default")
    public EstimatePdfTemplateResponse updateDefaultTemplate(
            @Valid @RequestBody UpdateEstimatePdfTemplateRequest request) {
        return estimatePdfTemplateService.updateDefaultTemplate(request);
    }

    @PostMapping(value = "/preview-pdf", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> previewPdfTemplate(
            @Valid @RequestBody PreviewEstimatePdfTemplateRequest request) {
        estimatePdfTemplateAccessService.requireAdminAccess();

        byte[] pdfBytes = estimatePdfGenerationService.generatePreviewPdf(request);

        String filename = "estimate-preview-"
                + request.getBidRevisionId()
                + ".pdf";

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

    @GetMapping("/designer-registry")
    public EstimatePdfDesignerRegistryResponse getDesignerRegistry() {
        estimatePdfTemplateAccessService.requireAdminAccess();

        return designerRegistryService.getRegistry();
    }
}