package com.glassgang.pmworkflow.estimate.pdf;

import com.glassgang.pmworkflow.estimate.pdf.model.EstimatePdfJobBlock;
import com.glassgang.pmworkflow.estimate.pdf.model.EstimatePdfModel;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class EstimatePdfHtmlPreviewService {

    private static final String DEFAULT_TEMPLATE_PATH =
            "templates/estimate/pdf/default-estimate-template.html";

    private final EstimatePdfModelBuilder estimatePdfModelBuilder;

    public EstimatePdfHtmlPreviewService(EstimatePdfModelBuilder estimatePdfModelBuilder) {
        this.estimatePdfModelBuilder = estimatePdfModelBuilder;
    }

    public String renderHtml(UUID bidRevisionId) {
        EstimatePdfModel model = estimatePdfModelBuilder.build(bidRevisionId);

        Map<String, Object> context = new HashMap<>();
        context.put("model", model);
        context.put("money", moneyLambda());
        context.put("label", labelLambda());
        context.put("hasCompany", hasCompany(model));
        context.put("hasTaxRate", hasTaxRate(model));
        context.put("hasJobAddress", hasJobAddress(model));

        Template template = Mustache.compiler()
                .defaultValue("")
                .compile(loadDefaultTemplate());

        StringWriter writer = new StringWriter();
        template.execute(context, writer);

        return writer.toString();
    }

    private String loadDefaultTemplate() {
        ClassPathResource resource = new ClassPathResource(DEFAULT_TEMPLATE_PATH);

        try {
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load estimate PDF HTML template", ex);
        }
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

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}