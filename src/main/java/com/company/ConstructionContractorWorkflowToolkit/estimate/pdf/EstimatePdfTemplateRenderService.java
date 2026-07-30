package com.company.ConstructionContractorWorkflowToolkit.estimate.pdf;

import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.pdf.PreviewEstimatePdfTemplateRequest;
import com.company.ConstructionContractorWorkflowToolkit.estimate.entity.EstimatePdfTemplate;
import com.company.ConstructionContractorWorkflowToolkit.estimate.pdf.model.EstimatePdfJobBlock;
import com.company.ConstructionContractorWorkflowToolkit.estimate.pdf.model.EstimatePdfModel;
import com.company.ConstructionContractorWorkflowToolkit.estimate.pdf.model.EstimatePdfPrintableRowPartition;
import com.company.ConstructionContractorWorkflowToolkit.estimate.repository.EstimatePdfTemplateRepository;
import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.BidRoundingMode;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class EstimatePdfTemplateRenderService {

    private static final String DEFAULT_CLASSPATH_TEMPLATE_PATH = "templates/estimate/pdf/default-estimate-template.html";

    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final EstimatePdfModelBuilder estimatePdfModelBuilder;
    private final EstimatePdfTemplateRepository estimatePdfTemplateRepository;
    private final EstimatePdfTemplateValidationService estimatePdfTemplateValidationService;
    private final EstimatePdfProtectedBlockRenderService protectedBlockRenderService;
    private final EstimatePdfPrintableRowPartitionService printableRowPartitionService;

    public EstimatePdfTemplateRenderService(
            EstimatePdfModelBuilder estimatePdfModelBuilder,
            EstimatePdfTemplateRepository estimatePdfTemplateRepository,
            EstimatePdfTemplateValidationService estimatePdfTemplateValidationService,
            EstimatePdfProtectedBlockRenderService protectedBlockRenderService,
            EstimatePdfPrintableRowPartitionService printableRowPartitionService) {

        this.estimatePdfModelBuilder = estimatePdfModelBuilder;
        this.estimatePdfTemplateRepository = estimatePdfTemplateRepository;
        this.estimatePdfTemplateValidationService = estimatePdfTemplateValidationService;
        this.protectedBlockRenderService = protectedBlockRenderService;
        this.printableRowPartitionService = printableRowPartitionService;
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

    private String renderTemplate(
            UUID bidRevisionId,
            String htmlTemplate,
            String cssTemplate) {

        EstimatePdfModel model = estimatePdfModelBuilder.build(bidRevisionId);

        EstimatePdfPrintableRowPartition printableRowPartition = printableRowPartitionService.partition(model);

        String htmlWithProtectedBlocks = protectedBlockRenderService.applyProtectedBlocks(htmlTemplate);

        String finalTemplate = injectCss(htmlWithProtectedBlocks, cssTemplate);

        Template template = Mustache.compiler()
                .defaultValue("")
                .compile(finalTemplate);

        StringWriter writer = new StringWriter();

        template.execute(
                buildMustacheContext(model, printableRowPartition),
                writer);

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

    private Map<String, Object> buildMustacheContext(
            EstimatePdfModel model,
            EstimatePdfPrintableRowPartition printableRowPartition) {

        Map<String, Object> context = new HashMap<>();

        context.put("model", model);
        context.put(
                "money",
                moneyLambda(model.getRoundingMode()));
        context.put("label", labelLambda());
        context.put("dateOnly", dateOnlyLambda());
        context.put(
                "formatQuantity",
                quantityLambda(model.getRoundingMode()));
        context.put("hasCompany", hasCompany(model));
        context.put("hasTaxRate", hasTaxRate(model));
        context.put("hasJobAddress", hasJobAddress(model));

        context.put(
                "mainPrintableRows",
                printableRowPartition.getMainRows());

        context.put(
                "finalCarryPrintableRows",
                printableRowPartition.getFinalCarryRows());

        context.put(
                "hasFinalCarryPrintableRows",
                !printableRowPartition.getFinalCarryRows().isEmpty());

        context.put(
                "hasMainPrintableRows",
                !printableRowPartition.getMainRows().isEmpty());

        return context;
    }

    private Mustache.Lambda moneyLambda(
            BidRoundingMode roundingMode) {

        return (Template.Fragment fragment, java.io.Writer writer) -> {
            String rawValue = fragment.execute();

            if (rawValue == null || rawValue.isBlank()) {
                return;
            }

            writer.write(
                    formatMoney(
                            rawValue.trim(),
                            roundingMode));
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

    private Mustache.Lambda dateOnlyLambda() {
        return (Template.Fragment fragment, java.io.Writer writer) -> {
            String rawValue = fragment.execute();

            if (rawValue == null || rawValue.isBlank()) {
                return;
            }

            writer.write(formatDateOnly(rawValue.trim()));
        };
    }

    private Mustache.Lambda quantityLambda(
            BidRoundingMode roundingMode) {

        return (Template.Fragment fragment, java.io.Writer writer) -> {
            String rawValue = fragment.execute();

            if (rawValue == null || rawValue.isBlank()) {
                return;
            }

            writer.write(
                    formatQuantity(
                            rawValue.trim(),
                            roundingMode));
        };
    }

    private String formatMoney(
            String rawValue,
            BidRoundingMode roundingMode) {

        try {
            BigDecimal value = new BigDecimal(rawValue);

            if (isWholeRoundingMode(roundingMode)) {
                return "$"
                        + value.setScale(
                                0,
                                RoundingMode.CEILING)
                                .toPlainString();
            }

            return "$"
                    + value.setScale(
                            2,
                            RoundingMode.HALF_UP)
                            .toPlainString();

        } catch (NumberFormatException exception) {
            return rawValue;
        }
    }

    private String formatDateOnly(String rawValue) {
        LocalDate date = parseDate(rawValue);

        if (date == null) {
            return rawValue;
        }

        return DATE_ONLY_FORMATTER.format(date);
    }

    private LocalDate parseDate(String rawValue) {
        try {
            return OffsetDateTime.parse(rawValue).toLocalDate();
        } catch (DateTimeParseException ignored) {
            // Try the next supported representation.
        }

        try {
            return ZonedDateTime.parse(rawValue).toLocalDate();
        } catch (DateTimeParseException ignored) {
            // Try the next supported representation.
        }

        try {
            return LocalDateTime.parse(rawValue).toLocalDate();
        } catch (DateTimeParseException ignored) {
            // Try the next supported representation.
        }

        try {
            return LocalDate.parse(rawValue);
        } catch (DateTimeParseException ignored) {
            // Try an ISO date prefix below.
        }

        if (rawValue.length() >= 10) {
            try {
                return LocalDate.parse(rawValue.substring(0, 10));
            } catch (DateTimeParseException ignored) {
                // Return null when the value is not a supported date.
            }
        }

        return null;
    }

    private String formatQuantity(
            String rawValue,
            BidRoundingMode roundingMode) {

        try {
            BigDecimal value = new BigDecimal(rawValue);

            if (isWholeRoundingMode(roundingMode)) {
                return value.setScale(
                        0,
                        RoundingMode.CEILING)
                        .toPlainString();
            }

            return value.stripTrailingZeros()
                    .toPlainString();

        } catch (NumberFormatException exception) {
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
            return new String(
                    resource.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Failed to load estimate PDF HTML template",
                    ex);
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private boolean isWholeRoundingMode(
            BidRoundingMode roundingMode) {

        BidRoundingMode effectiveMode = roundingMode != null
                ? roundingMode
                : BidRoundingMode.WHOLE;

        return effectiveMode == BidRoundingMode.WHOLE;
    }
}