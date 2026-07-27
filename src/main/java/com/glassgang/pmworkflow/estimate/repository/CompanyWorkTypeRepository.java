package com.glassgang.pmworkflow.estimate.repository;

import com.glassgang.pmworkflow.estimate.entity.CompanyWorkType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyWorkTypeRepository
        extends JpaRepository<CompanyWorkType, UUID> {

    Optional<CompanyWorkType> findByCompanyWorkTypeIdAndIsDeletedFalse(
            UUID companyWorkTypeId);

    Optional<CompanyWorkType> findByNormalizedCodeAndIsDeletedFalse(
            String normalizedCode);

    boolean existsByNormalizedCodeAndIsDeletedFalse(
            String normalizedCode);

    boolean existsByNormalizedCodeAndIsDeletedFalseAndCompanyWorkTypeIdNot(
            String normalizedCode,
            UUID companyWorkTypeId);

    boolean existsByParentWorkType_CompanyWorkTypeIdAndIsDeletedFalse(
            UUID parentWorkTypeId);

    List<CompanyWorkType> findByIsDeletedFalseOrderByDisplayOrderAsc();

    List<CompanyWorkType> findByDivisionCodeAndIsDeletedFalseOrderByDisplayOrderAsc(
            String divisionCode);

    long countByIsDeletedFalse();

    long countByDivisionCodeAndIsDeletedFalse(
            String divisionCode);

    long countByDivisionCodeAndIsDeletedFalseAndIsActiveTrue(
            String divisionCode);

    @Query("""
            select coalesce(max(workType.displayOrder), -1)
            from CompanyWorkType workType
            where workType.isDeleted = false
            """)
    Integer findMaximumActiveDisplayOrder();

    @Query("""
            select workType
            from CompanyWorkType workType
            where workType.isDeleted = false
              and workType.isActive = true
              and workType.level between 4 and 5
              and exists (
                  select division.companyWorkTypeDivisionId
                  from CompanyWorkTypeDivision division
                  where division.divisionCode = workType.divisionCode
                    and division.isEnabled = true
              )
              and (
                  (
                      :codeQuery <> ''
                      and workType.normalizedCode like concat(:codeQuery, '%')
                  )
                  or lower(workType.name)
                      like lower(concat('%', :textQuery, '%'))
                  or lower(coalesce(workType.searchAliases, ''))
                      like lower(concat('%', :textQuery, '%'))
              )
            order by
                case
                    when :codeQuery <> ''
                         and workType.normalizedCode = :codeQuery
                        then 0
                    when :codeQuery <> ''
                         and workType.normalizedCode
                             like concat(:codeQuery, '%')
                        then 1
                    when lower(workType.name) = lower(:textQuery)
                        then 2
                    when lower(workType.name)
                             like lower(concat(:textQuery, '%'))
                        then 3
                    when lower(coalesce(workType.searchAliases, ''))
                             like lower(concat(:textQuery, '%'))
                        then 4
                    else 5
                end,
                workType.displayOrder asc,
                workType.code asc
            """)
    List<CompanyWorkType> searchSelectableWorkTypes(
            @Param("textQuery") String textQuery,
            @Param("codeQuery") String codeQuery,
            Pageable pageable);
}