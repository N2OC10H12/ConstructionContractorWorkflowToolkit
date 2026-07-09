package com.glassgang.pmworkflow.estimate.pdf;

import com.glassgang.pmworkflow.estimate.dto.pdf.EstimatePdfDesignerRegistryResponse;
import com.glassgang.pmworkflow.estimate.dto.pdf.EstimatePdfTemplateResponse;
import com.glassgang.pmworkflow.estimate.dto.pdf.PreviewEstimatePdfTemplateRequest;
import com.glassgang.pmworkflow.estimate.dto.pdf.UpdateEstimatePdfTemplateRequest;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/estimates/pdf-templates")
public class EstimatePdfTemplateController {

    private final EstimatePdfTemplateService estimatePdfTemplateService;
    private final EstimatePdfTemplateRenderService estimatePdfTemplateRenderService;
    private final EstimatePdfTemplateAccessService estimatePdfTemplateAccessService;
    private final EstimatePdfTemplateDesignerRegistryService designerRegistryService;

    public EstimatePdfTemplateController(
            EstimatePdfTemplateService estimatePdfTemplateService,
            EstimatePdfTemplateRenderService estimatePdfTemplateRenderService,
            EstimatePdfTemplateAccessService estimatePdfTemplateAccessService,
            EstimatePdfTemplateDesignerRegistryService designerRegistryService) {
        this.estimatePdfTemplateService = estimatePdfTemplateService;
        this.estimatePdfTemplateRenderService = estimatePdfTemplateRenderService;
        this.estimatePdfTemplateAccessService = estimatePdfTemplateAccessService;
        this.designerRegistryService = designerRegistryService;
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

    @PostMapping(value = "/preview", produces = MediaType.TEXT_HTML_VALUE)
    public String previewTemplate(@Valid @RequestBody PreviewEstimatePdfTemplateRequest request) {
        estimatePdfTemplateAccessService.requireAdminAccess();

        return estimatePdfTemplateRenderService.renderUnsavedTemplate(request);
    }

    @GetMapping("/designer-registry")
    public EstimatePdfDesignerRegistryResponse getDesignerRegistry() {
        estimatePdfTemplateAccessService.requireAdminAccess();
        return designerRegistryService.getRegistry();
    }
}