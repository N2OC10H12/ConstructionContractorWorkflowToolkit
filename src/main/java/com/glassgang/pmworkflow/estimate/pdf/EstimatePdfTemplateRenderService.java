package com.glassgang.pmworkflow.estimate.pdf;

import com.glassgang.pmworkflow.estimate.dto.pdf.PreviewEstimatePdfTemplateRequest;
import com.glassgang.pmworkflow.estimate.entity.EstimatePdfTemplate;
import com.glassgang.pmworkflow.estimate.pdf.model.EstimatePdfJobBlock;
import com.glassgang.pmworkflow.estimate.pdf.model.EstimatePdfModel;
import com.glassgang.pmworkflow.estimate.repository.EstimatePdfTemplateRepository;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class EstimatePdfTemplateRenderService {

    private static final String DEFAULT_CLASSPATH_TEMPLATE_PATH = "templates/estimate/pdf/default-estimate-template.html";

    private final EstimatePdfModelBuilder estimatePdfModelBuilder;
    private final EstimatePdfTemplateRepository estimatePdfTemplateRepository;
    private final EstimatePdfTemplateValidationService estimatePdfTemplateValidationService;
    private final EstimatePdfProtectedBlockRenderService protectedBlockRenderService;

    public EstimatePdfTemplateRenderService(
            EstimatePdfModelBuilder estimatePdfModelBuilder,
            EstimatePdfTemplateRepository estimatePdfTemplateRepository,
            EstimatePdfTemplateValidationService estimatePdfTemplateValidationService,
            EstimatePdfProtectedBlockRenderService protectedBlockRenderService) {
        this.estimatePdfModelBuilder = estimatePdfModelBuilder;
        this.estimatePdfTemplateRepository = estimatePdfTemplateRepository;
        this.estimatePdfTemplateValidationService = estimatePdfTemplateValidationService;
        this.protectedBlockRenderService = protectedBlockRenderService;
    }

    @Transactional(readOnly = true)
    public String renderSavedTemplate(UUID bidRevisionId) {
        EstimatePdfTemplate template = estimatePdfTemplateRepository
                .findFirstByIsDefaultTrueAndIsActiveTrueAndIsDeletedFalse()
                .orElse(null);

        if (template == null || !hasText(template.getHtmlTemplate())) {
            return renderTemplate(
                    bidRevisionId,
                    loadClasspathDefaultTemplate(),
                    null);
        }

        return renderTemplate(
                bidRevisionId,
                template.getHtmlTemplate(),
                template.getCssTemplate());
    }

    @Transactional(readOnly = true)
    public String renderUnsavedTemplate(PreviewEstimatePdfTemplateRequest request) {
        estimatePdfTemplateValidationService.validatePreview(request);

        return renderTemplate(
                request.getBidRevisionId(),
                request.getHtmlTemplate(),
                request.getCssTemplate());
    }

    private String renderTemplate(UUID bidRevisionId, String htmlTemplate, String cssTemplate) {
        EstimatePdfModel model = estimatePdfModelBuilder.build(bidRevisionId);

        String htmlWithProtectedBlocks = protectedBlockRenderService.applyProtectedBlocks(htmlTemplate);
        String finalTemplate = injectCss(htmlWithProtectedBlocks, cssTemplate);

        Template template = Mustache.compiler()
                .defaultValue("")
                .compile(finalTemplate);

        StringWriter writer = new StringWriter();
        template.execute(buildMustacheContext(model), writer);

        return writer.toString();
    }

    public String injectCss(String htmlTemplate, String cssTemplate) {
        if (htmlTemplate == null) {
            htmlTemplate = "";
        }

        if (!hasText(cssTemplate)) {
            return htmlTemplate;
        }

        String styleBlock = "<style>\n" + cssTemplate + "\n</style>\n";

        String lowerHtml = htmlTemplate.toLowerCase(Locale.ROOT);
        int headCloseIndex = lowerHtml.indexOf("</head>");

        if (headCloseIndex >= 0) {
            return htmlTemplate.substring(0, headCloseIndex)
                    + styleBlock
                    + htmlTemplate.substring(headCloseIndex);
        }

        return styleBlock + htmlTemplate;
    }

    private Map<String, Object> buildMustacheContext(EstimatePdfModel model) {
        Map<String, Object> context = new HashMap<>();

        context.put("model", model);
        context.put("money", moneyLambda());
        context.put("label", labelLambda());
        context.put("hasCompany", hasCompany(model));
        context.put("hasTaxRate", hasTaxRate(model));
        context.put("hasJobAddress", hasJobAddress(model));

        return context;
    }

    private Mustache.Lambda moneyLambda() {
        return (Template.Fragment fragment, java.io.Writer writer) -> {
            String rawValue = fragment.execute();

            if (rawValue == null || rawValue.isBlank()) {
                return;
            }

            writer.write(formatMoney(rawValue.trim()));
        };
    }

    private Mustache.Lambda labelLambda() {
        return (Template.Fragment fragment, java.io.Writer writer) -> {
            String rawValue = fragment.execute();

            if (rawValue == null || rawValue.isBlank()) {
                return;
            }

            writer.write(toDisplayLabel(rawValue.trim()));
        };
    }

    private String formatMoney(String rawValue) {
        try {
            BigDecimal value = new BigDecimal(rawValue);
            return "$" + value.setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException ex) {
            return rawValue;
        }
    }

    private String toDisplayLabel(String rawValue) {
        String normalized = rawValue.replace("_", " ").toLowerCase();
        String[] parts = normalized.split("\\s+");

        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }

            if (!result.isEmpty()) {
                result.append(" ");
            }

            result.append(Character.toUpperCase(part.charAt(0)));

            if (part.length() > 1) {
                result.append(part.substring(1));
            }
        }

        return result.toString();
    }

    private boolean hasCompany(EstimatePdfModel model) {
        return model.getCompany() != null
                && hasText(model.getCompany().getCompanyName());
    }

    private boolean hasTaxRate(EstimatePdfModel model) {
        EstimatePdfJobBlock job = model.getJob();

        return job != null
                && (hasText(job.getDefaultTaxRateName())
                        || job.getDefaultTaxRatePercent() != null);
    }

    private boolean hasJobAddress(EstimatePdfModel model) {
        EstimatePdfJobBlock job = model.getJob();

        return job != null
                && (hasText(job.getAddressLine1())
                        || hasText(job.getAddressLine2())
                        || hasText(job.getCity())
                        || hasText(job.getState())
                        || hasText(job.getPostalCode())
                        || hasText(job.getCountry()));
    }

    private String loadClasspathDefaultTemplate() {
        ClassPathResource resource = new ClassPathResource(DEFAULT_CLASSPATH_TEMPLATE_PATH);

        try {
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load estimate PDF HTML template", ex);
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}