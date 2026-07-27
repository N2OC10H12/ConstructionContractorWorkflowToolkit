package com.glassgang.pmworkflow.estimate.seed;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glassgang.pmworkflow.estimate.entity.CompanyWorkType;
import com.glassgang.pmworkflow.estimate.entity.CompanyWorkTypeDivision;
import com.glassgang.pmworkflow.estimate.enums.CompanyWorkTypeSourceType;
import com.glassgang.pmworkflow.estimate.repository.CompanyWorkTypeDivisionRepository;
import com.glassgang.pmworkflow.estimate.repository.CompanyWorkTypeRepository;
import com.glassgang.pmworkflow.estimate.util.CompanyWorkTypeCodeUtil;
import jakarta.persistence.EntityManager;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MasterFormat2016JsonSeeder implements ApplicationRunner {

    private static final String CATALOG_PATH = "catalog/estimate/masterformat-2016-with-levels.json";

    private static final Logger log = LoggerFactory.getLogger(MasterFormat2016JsonSeeder.class);

    private static final String SOURCE_EDITION = "2016";

    private static final int MIN_LEVEL = 1;
    private static final int MAX_LEVEL = 5;

    private final ObjectMapper objectMapper;
    private final CompanyWorkTypeRepository companyWorkTypeRepository;
    private final CompanyWorkTypeDivisionRepository divisionRepository;
    private final EntityManager entityManager;

    public MasterFormat2016JsonSeeder(
            ObjectMapper objectMapper,
            CompanyWorkTypeRepository companyWorkTypeRepository,
            CompanyWorkTypeDivisionRepository divisionRepository,
            EntityManager entityManager) {
        this.objectMapper = objectMapper;
        this.companyWorkTypeRepository = companyWorkTypeRepository;
        this.divisionRepository = divisionRepository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("MasterFormat 2016 seeder started");

        long existingWorkTypeCount = companyWorkTypeRepository.countByIsDeletedFalse();

        log.info(
                "Existing Company Work Type count: {}",
                existingWorkTypeCount);

        if (existingWorkTypeCount > 0) {
            log.info("MasterFormat 2016 seed skipped");
            return;
        }

        List<MasterFormatSeedEntry> entries = loadSeedEntries();

        log.info(
                "Loaded {} MasterFormat 2016 seed entries",
                entries.size());

        validateSeedEntries(entries);

        LocalDateTime now = LocalDateTime.now();

        Map<String, CompanyWorkTypeDivision> divisionsByCode = createMissingDivisions(entries, now);

        entityManager.flush();

        log.info(
                "Created or found {} division records",
                divisionsByCode.size());

        createWorkTypes(entries, divisionsByCode.keySet(), now);

        entityManager.flush();

        log.info(
                "MasterFormat 2016 seed completed with {} work types",
                entries.size());
    }

    private Map<String, CompanyWorkTypeDivision> createMissingDivisions(
            List<MasterFormatSeedEntry> entries,
            LocalDateTime now) {

        Map<String, CompanyWorkTypeDivision> divisionsByCode = divisionRepository.findAllByOrderByDivisionCodeAsc()
                .stream()
                .collect(Collectors.toMap(
                        CompanyWorkTypeDivision::getDivisionCode,
                        Function.identity(),
                        (first, ignored) -> first,
                        LinkedHashMap::new));

        for (MasterFormatSeedEntry entry : entries) {
            if (entry.level() != 1) {
                continue;
            }

            String divisionCode = CompanyWorkTypeCodeUtil.deriveDivisionCode(entry.code());

            if (divisionsByCode.containsKey(divisionCode)) {
                /*
                 * Preserve any existing company-edited division name and
                 * enabled state.
                 */
                continue;
            }

            CompanyWorkTypeDivision division = new CompanyWorkTypeDivision();

            division.setCompanyWorkTypeDivisionId(UUID.randomUUID());
            division.setDivisionCode(divisionCode);
            division.setDivisionName(entry.label().trim());

            /*
             * Divisions must be explicitly enabled by an administrator.
             */
            division.setIsEnabled(false);
            division.setEnabledAtUtc(null);
            division.setEnabledByUserId(null);

            division.setCreatedAtUtc(now);
            division.setUpdatedAtUtc(now);

            entityManager.persist(division);
            divisionsByCode.put(divisionCode, division);
        }

        return divisionsByCode;
    }

    private void createWorkTypes(
            List<MasterFormatSeedEntry> entries,
            Set<String> availableDivisionCodes,
            LocalDateTime now) {

        Map<Integer, CompanyWorkType> latestWorkTypeByLevel = new HashMap<>();

        int displayOrder = 0;

        for (MasterFormatSeedEntry entry : entries) {
            String normalizedCode = CompanyWorkTypeCodeUtil.normalize(entry.code());

            String formattedCode = CompanyWorkTypeCodeUtil.format(entry.code());

            String divisionCode = CompanyWorkTypeCodeUtil.deriveDivisionCode(entry.code());

            if (!availableDivisionCodes.contains(divisionCode)) {
                throw new IllegalStateException(
                        "No division configuration exists for work type code "
                                + entry.code());
            }

            clearCurrentAndDeeperLevels(
                    latestWorkTypeByLevel,
                    entry.level());

            CompanyWorkType parentWorkType = findNearestParent(
                    latestWorkTypeByLevel,
                    entry.level());

            CompanyWorkType workType = new CompanyWorkType();

            workType.setCompanyWorkTypeId(UUID.randomUUID());

            workType.setCode(formattedCode);
            workType.setNormalizedCode(normalizedCode);
            workType.setName(entry.label().trim());

            workType.setLevel(entry.level());
            workType.setDivisionCode(divisionCode);
            workType.setParentWorkType(parentWorkType);

            workType.setSourceType(
                    CompanyWorkTypeSourceType.MASTERFORMAT_IMPORT);
            workType.setSourceEdition(SOURCE_EDITION);
            workType.setOriginalName(entry.label().trim());

            workType.setSearchAliases(null);
            workType.setDisplayOrder(displayOrder++);

            workType.setIsActive(true);
            workType.setIsDeleted(false);

            workType.setCreatedAtUtc(now);
            workType.setUpdatedAtUtc(now);

            entityManager.persist(workType);

            latestWorkTypeByLevel.put(entry.level(), workType);
        }
    }

    private void clearCurrentAndDeeperLevels(
            Map<Integer, CompanyWorkType> latestWorkTypeByLevel,
            int currentLevel) {

        for (int level = currentLevel; level <= MAX_LEVEL; level++) {
            latestWorkTypeByLevel.remove(level);
        }
    }

    private CompanyWorkType findNearestParent(
            Map<Integer, CompanyWorkType> latestWorkTypeByLevel,
            int currentLevel) {

        for (int level = currentLevel - 1; level >= MIN_LEVEL; level--) {

            CompanyWorkType candidate = latestWorkTypeByLevel.get(level);

            if (candidate != null) {
                return candidate;
            }
        }

        return null;
    }

    private List<MasterFormatSeedEntry> loadSeedEntries() {
        ClassPathResource resource = new ClassPathResource(CATALOG_PATH);

        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<MasterFormatSeedEntry>>() {
                    });
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Failed to load MasterFormat catalog resource: "
                            + CATALOG_PATH,
                    ex);
        }
    }

    private void validateSeedEntries(
            List<MasterFormatSeedEntry> entries) {

        if (entries == null || entries.isEmpty()) {
            throw new IllegalStateException(
                    "MasterFormat catalog is empty");
        }

        Set<String> normalizedCodes = new HashSet<>();
        Set<String> divisionCodes = new HashSet<>();

        for (int index = 0; index < entries.size(); index++) {
            MasterFormatSeedEntry entry = entries.get(index);

            if (entry == null) {
                throw new IllegalStateException(
                        "MasterFormat catalog contains a null row at index "
                                + index);
            }

            if (entry.code() == null || entry.code().isBlank()) {
                throw new IllegalStateException(
                        "MasterFormat code is missing at index "
                                + index);
            }

            if (entry.label() == null || entry.label().isBlank()) {
                throw new IllegalStateException(
                        "MasterFormat label is missing for code "
                                + entry.code());
            }

            if (entry.level() == null
                    || entry.level() < MIN_LEVEL
                    || entry.level() > MAX_LEVEL) {
                throw new IllegalStateException(
                        "MasterFormat level must be between 1 and 5 for code "
                                + entry.code());
            }

            String normalizedCode;

            try {
                normalizedCode = CompanyWorkTypeCodeUtil.normalize(entry.code());
            } catch (IllegalArgumentException ex) {
                throw new IllegalStateException(
                        "Invalid MasterFormat code: "
                                + entry.code(),
                        ex);
            }

            if (!normalizedCodes.add(normalizedCode)) {
                throw new IllegalStateException(
                        "Duplicate normalized MasterFormat code: "
                                + entry.code());
            }

            if (entry.level() == 1) {
                divisionCodes.add(
                        CompanyWorkTypeCodeUtil.deriveDivisionCode(
                                entry.code()));
            }
        }

        if (divisionCodes.isEmpty()) {
            throw new IllegalStateException(
                    "MasterFormat catalog contains no level-1 divisions");
        }

        for (MasterFormatSeedEntry entry : entries) {
            String divisionCode = CompanyWorkTypeCodeUtil.deriveDivisionCode(
                    entry.code());

            if (!divisionCodes.contains(divisionCode)) {
                throw new IllegalStateException(
                        "Work type has no level-1 division entry: "
                                + entry.code());
            }
        }
    }

    private record MasterFormatSeedEntry(
            String code,
            String label,
            Integer level) {
    }
}