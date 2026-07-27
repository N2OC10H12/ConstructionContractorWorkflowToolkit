package com.company.ConstructionContractorWorkflowToolkit.estimate.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class BidNumberService {

    private final JdbcTemplate jdbcTemplate;

    public BidNumberService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String generateNextBidNumber() {
        Long value = jdbcTemplate.queryForObject(
                "SELECT nextval('estimate.bid_number_seq')",
                Long.class
        );

        return "B" + value;
    }

    public String generateNextJobNumber() {
        Long value = jdbcTemplate.queryForObject(
                "SELECT nextval('estimate.job_number_seq')",
                Long.class
        );

        return "J" + value;
    }

    public String buildRevisionDisplayName(
            String bidNumber,
            String departmentCode,
            Integer revisionNumber
    ) {
        LocalDate now = LocalDate.now();

        return bidNumber
                + "."
                + departmentCode
                + "."
                + now.getMonthValue()
                + "."
                + now.getYear()
                + ".R"
                + revisionNumber;
    }
}