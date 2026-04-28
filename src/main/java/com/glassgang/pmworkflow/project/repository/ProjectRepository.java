package com.glassgang.pmworkflow.project.repository;

import com.glassgang.pmworkflow.project.entity.Project;
import com.glassgang.pmworkflow.user.entity.AppUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    List<Project> findByOwner(AppUser owner);

    @EntityGraph(attributePaths = {"steps"})
    @Query("select p from Project p where p.id = :id")
    Optional<Project> findWithStepsById(@Param("id") UUID id);
    List<Project> findByOwner_Id(UUID ownerId);
}