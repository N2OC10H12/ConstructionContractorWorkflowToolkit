package com.company.ConstructionContractorWorkflowToolkit.estimate.pdf;

import com.company.ConstructionContractorWorkflowToolkit.estimate.entity.EstimatePdfTemplate;
import com.company.ConstructionContractorWorkflowToolkit.estimate.repository.EstimatePdfTemplateRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class EstimatePdfTemplateSeeder implements ApplicationRunner {

    
    private static final String DEFAULT_TEMPLATE_CODE = "DEFAULT_ESTIMATE_TEMPLATE";

    private static final String DEFAULT_HTML_TEMPLATE_PATH = "templates/estimate/pdf/default-estimate-template.html";

    private static final String DEFAULT_CSS_TEMPLATE_PATH = "templates/estimate/pdf/default-estimate-template.css";

    private static final String DEFAULT_TEMPLATE_DEFINITION_PATH = "templates/estimate/pdf/default-estimate-template-definition.json";

    private final EstimatePdfTemplateRepository estimatePdfTemplateRepository;

    public EstimatePdfTemplateSeeder(EstimatePdfTemplateRepository estimatePdfTemplateRepository) {
        this.estimatePdfTemplateRepository = estimatePdfTemplateRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (estimatePdfTemplateRepository
                .findByCodeAndIsDeletedFalse(DEFAULT_TEMPLATE_CODE)
                .isPresent()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        EstimatePdfTemplate template = new EstimatePdfTemplate();

        template.setEstimatePdfTemplateId(UUID.randomUUID());
        template.setCode(DEFAULT_TEMPLATE_CODE);
        template.setName("Default Estimate Template");

        template.setHtmlTemplate(loadClasspathResource(DEFAULT_HTML_TEMPLATE_PATH));
        template.setCssTemplate(loadClasspathResource(DEFAULT_CSS_TEMPLATE_PATH));
        template.setTemplateDefinitionJson(loadClasspathResource(DEFAULT_TEMPLATE_DEFINITION_PATH));

        template.setIsDefault(true);
        template.setIsActive(true);
        template.setIsDeleted(false);

        template.setCreatedAtUtc(now);
        template.setUpdatedAtUtc(now);

        template.setVersionNumber(1);

        estimatePdfTemplateRepository.save(template);
    }

    @NonNull
    private String loadClasspathResource(@NonNull String path) {
        ClassPathResource resource = new ClassPathResource(path);

        try {
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load classpath resource: " + path, ex);
        }
    }
}