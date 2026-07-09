package com.glassgang.pmworkflow.estimate.repository;

import com.glassgang.pmworkflow.estimate.entity.ConstructionObjectType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConstructionObjectTypeRepository extends JpaRepository<ConstructionObjectType, UUID> {

    Optional<ConstructionObjectType> findByConstructionObjectTypeIdAndIsDeletedFalseAndIsActiveTrue(
            UUID constructionObjectTypeId);

    Optional<ConstructionObjectType> findByConstructionObjectTypeIdAndIsDeletedFalse(
            UUID constructionObjectTypeId);

    Optional<ConstructionObjectType> findByCodeAndIsDeletedFalse(String code);

    boolean existsByCodeAndIsDeletedFalse(String code);

    List<ConstructionObjectType> findByIsDeletedFalseOrderByCodeAsc();

    List<ConstructionObjectType> findByIsDeletedFalseAndIsActiveTrueOrderByCodeAsc();

    Optional<ConstructionObjectType> findByCode(String code);

    boolean existsByCode(String code);
}