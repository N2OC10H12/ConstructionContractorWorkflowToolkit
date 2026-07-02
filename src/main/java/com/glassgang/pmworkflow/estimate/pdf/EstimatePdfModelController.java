package com.glassgang.pmworkflow.estimate.pdf;

import com.glassgang.pmworkflow.estimate.pdf.model.EstimatePdfModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/estimates/bids/revisions")
public class EstimatePdfModelController {

    private final EstimatePdfModelBuilder estimatePdfModelBuilder;

    public EstimatePdfModelController(EstimatePdfModelBuilder estimatePdfModelBuilder) {
        this.estimatePdfModelBuilder = estimatePdfModelBuilder;
    }

    @GetMapping("/{bidRevisionId}/pdf-model")
    public EstimatePdfModel getPdfModel(@PathVariable UUID bidRevisionId) {
        return estimatePdfModelBuilder.build(bidRevisionId);
    }
}