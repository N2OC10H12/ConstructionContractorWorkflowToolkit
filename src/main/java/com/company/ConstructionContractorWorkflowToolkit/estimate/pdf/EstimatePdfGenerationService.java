package com.company.ConstructionContractorWorkflowToolkit.estimate.pdf;

import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.pdf.PreviewEstimatePdfTemplateRequest;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.Loader;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.UUID;

@Service
public class EstimatePdfGenerationService {

    private static final String PREVIEW_WATERMARK_TEXT = "DRAFT PREVIEW \u2013 NOT FOR DISTRIBUTION";

    private static final float PREVIEW_RENDER_DPI = 144.0f;
    private static final float PREVIEW_JPEG_QUALITY = 0.88f;
    private static final float WATERMARK_OPACITY = 0.16f;
    private static final double WATERMARK_ROTATION_RADIANS = Math.toRadians(-30.0);

    private final EstimatePdfTemplateRenderService estimatePdfTemplateRenderService;

    public EstimatePdfGenerationService(
            EstimatePdfTemplateRenderService estimatePdfTemplateRenderService) {
        this.estimatePdfTemplateRenderService = estimatePdfTemplateRenderService;
    }

    public byte[] generatePdf(UUID bidRevisionId) {
        String renderedHtml = estimatePdfTemplateRenderService.renderSavedTemplate(bidRevisionId);

        return generatePdfFromRenderedHtml(renderedHtml);
    }

    public byte[] generateSavedTemplatePreviewPdf(UUID bidRevisionId) {
        String renderedHtml = estimatePdfTemplateRenderService.renderSavedTemplate(bidRevisionId);

        return createProtectedPreviewPdf(
                generatePdfFromRenderedHtml(renderedHtml));
    }

    public byte[] generatePreviewPdf(
            PreviewEstimatePdfTemplateRequest request) {

        String renderedHtml = estimatePdfTemplateRenderService.renderUnsavedTemplate(request);

        return createProtectedPreviewPdf(
                generatePdfFromRenderedHtml(renderedHtml));
    }

    private byte[] generatePdfFromRenderedHtml(String renderedHtml) {
        String xhtml = toXhtml(renderedHtml);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();

            builder.withHtmlContent(xhtml, null);
            builder.toStream(outputStream);
            builder.run();

            return outputStream.toByteArray();
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Failed to generate estimate PDF",
                    exception);
        }
    }

    private byte[] createProtectedPreviewPdf(byte[] sourcePdfBytes) {
        try (
                PDDocument sourceDocument = Loader.loadPDF(sourcePdfBytes);
                PDDocument previewDocument = new PDDocument();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PDFRenderer pdfRenderer = new PDFRenderer(sourceDocument);

            for (int pageIndex = 0; pageIndex < sourceDocument.getNumberOfPages(); pageIndex++) {

                PDPage sourcePage = sourceDocument.getPage(pageIndex);
                PDRectangle sourceMediaBox = sourcePage.getMediaBox();

                BufferedImage pageImage = pdfRenderer.renderImageWithDPI(
                        pageIndex,
                        PREVIEW_RENDER_DPI,
                        ImageType.RGB);

                applyCheckerboardWatermark(pageImage);

                PDPage previewPage = new PDPage(
                        new PDRectangle(
                                sourceMediaBox.getWidth(),
                                sourceMediaBox.getHeight()));

                previewDocument.addPage(previewPage);

                PDImageXObject pageImageObject = JPEGFactory.createFromImage(
                        previewDocument,
                        pageImage,
                        PREVIEW_JPEG_QUALITY,
                        Math.round(PREVIEW_RENDER_DPI));

                try (PDPageContentStream contentStream = new PDPageContentStream(
                        previewDocument,
                        previewPage,
                        PDPageContentStream.AppendMode.OVERWRITE,
                        false,
                        false)) {

                    contentStream.drawImage(
                            pageImageObject,
                            0,
                            0,
                            previewPage.getMediaBox().getWidth(),
                            previewPage.getMediaBox().getHeight());
                }
            }

            applyPreviewPermissions(previewDocument);

            previewDocument.save(outputStream);

            return outputStream.toByteArray();
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Failed to create protected estimate PDF preview",
                    exception);
        }
    }

    private void applyCheckerboardWatermark(BufferedImage pageImage) {
        Graphics2D graphics = pageImage.createGraphics();

        try {
            graphics.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

            int pageWidth = pageImage.getWidth();
            int pageHeight = pageImage.getHeight();

            int fontSize = Math.max(30, pageWidth / 24);

            Font watermarkFont = new Font(
                    Font.SANS_SERIF,
                    Font.BOLD,
                    fontSize);

            graphics.setFont(watermarkFont);
            graphics.setColor(new Color(105, 105, 105));
            graphics.setComposite(
                    AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER,
                            WATERMARK_OPACITY));

            FontMetrics fontMetrics = graphics.getFontMetrics();
            int textWidth = fontMetrics.stringWidth(PREVIEW_WATERMARK_TEXT);
            int textHeight = fontMetrics.getHeight();

            int horizontalSpacing = Math.max(textWidth + fontSize * 3, pageWidth / 2);
            int verticalSpacing = Math.max(textHeight + fontSize * 4, pageHeight / 5);

            int rowIndex = 0;

            for (int y = -verticalSpacing; y < pageHeight + verticalSpacing; y += verticalSpacing) {

                int checkerOffset = rowIndex % 2 == 0
                        ? 0
                        : horizontalSpacing / 2;

                for (int x = -horizontalSpacing; x < pageWidth + horizontalSpacing; x += horizontalSpacing) {

                    drawRotatedWatermark(
                            graphics,
                            x + checkerOffset,
                            y,
                            textWidth,
                            textHeight);
                }

                rowIndex++;
            }
        } finally {
            graphics.dispose();
        }
    }

    private void drawRotatedWatermark(
            Graphics2D graphics,
            int x,
            int y,
            int textWidth,
            int textHeight) {

        AffineTransform originalTransform = graphics.getTransform();

        try {
            double centerX = x + textWidth / 2.0;
            double centerY = y + textHeight / 2.0;

            graphics.rotate(
                    WATERMARK_ROTATION_RADIANS,
                    centerX,
                    centerY);

            graphics.drawString(
                    PREVIEW_WATERMARK_TEXT,
                    x,
                    y + textHeight);
        } finally {
            graphics.setTransform(originalTransform);
        }
    }

    private void applyPreviewPermissions(
            PDDocument previewDocument) throws IOException {

        AccessPermission accessPermission = new AccessPermission();

        accessPermission.setCanPrint(false);
        accessPermission.setCanPrintFaithful(false);
        accessPermission.setCanModify(false);
        accessPermission.setCanModifyAnnotations(false);
        accessPermission.setCanFillInForm(false);
        accessPermission.setCanExtractContent(false);
        accessPermission.setCanExtractForAccessibility(false);
        accessPermission.setCanAssembleDocument(false);

        String ownerPassword = UUID.randomUUID().toString();

        StandardProtectionPolicy protectionPolicy = new StandardProtectionPolicy(
                ownerPassword,
                "",
                accessPermission);

        protectionPolicy.setEncryptionKeyLength(128);
        protectionPolicy.setPermissions(accessPermission);

        previewDocument.protect(protectionPolicy);
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