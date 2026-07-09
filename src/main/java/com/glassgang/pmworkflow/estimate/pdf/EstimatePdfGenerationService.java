package com.glassgang.pmworkflow.estimate.pdf;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

@Service
public class EstimatePdfGenerationService {

    private final EstimatePdfTemplateRenderService estimatePdfTemplateRenderService;

    public EstimatePdfGenerationService(
            EstimatePdfTemplateRenderService estimatePdfTemplateRenderService) {
        this.estimatePdfTemplateRenderService = estimatePdfTemplateRenderService;
    }

    public byte[] generatePdf(UUID bidRevisionId) {
        String html = toXhtml(
                estimatePdfTemplateRenderService.renderSavedTemplate(bidRevisionId));

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();

            return outputStream.toByteArray();
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to generate estimate PDF", exception);
        }
    }

    private String toXhtml(String html) {
        org.jsoup.nodes.Document document = Jsoup.parse(html);

        document.outputSettings()
                .syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml)
                .escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml)
                .prettyPrint(false);

        return document.html();
    }
}