package com.glassgang.pmworkflow.estimate.repository;

import com.glassgang.pmworkflow.estimate.entity.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ItemTypeRepository extends JpaRepository<ItemType, UUID> {

    Optional<ItemType> findByItemTypeIdAndIsDeletedFalseAndIsActiveTrue(UUID itemTypeId);
}