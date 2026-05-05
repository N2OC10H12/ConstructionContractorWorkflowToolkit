package com.glassgang.pmworkflow.project.repository;

import com.glassgang.pmworkflow.project.entity.Project;
import com.glassgang.pmworkflow.project.repository.projection.ProjectSummaryFlatRow;
import com.glassgang.pmworkflow.project.repository.projection.ProjectStepSummaryRow;
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

    List<Project> findByOwner_Id(UUID ownerId);

    boolean existsByOwner_Id(UUID ownerUserId);

    @EntityGraph(attributePaths = { "steps" })
    @Query("select p from Project p where p.id = :id")
    Optional<Project> findWithStepsById(@Param("id") UUID id);

    @Query("select p from Project p where p.id = :id")
    Optional<Project> findFreshById(@Param("id") UUID id);

    @EntityGraph(attributePaths = { "steps" })
    @Query("select distinct p from Project p")
    List<Project> findAllWithSteps();

    @EntityGraph(attributePaths = { "steps" })
    @Query("select distinct p from Project p where p.owner.id = :ownerId")
    List<Project> findByOwnerIdWithSteps(@Param("ownerId") UUID ownerId);

    // --- KEEP EXISTING (for later use if needed) ---
    @EntityGraph(attributePaths = { "steps", "steps.substeps" })
    @Query("select distinct p from Project p")
    List<Project> findAllWithStepsAndSubsteps();

    @EntityGraph(attributePaths = { "steps", "steps.substeps" })
    @Query("select distinct p from Project p where p.owner.id = :ownerId")
    List<Project> findByOwnerIdWithStepsAndSubsteps(@Param("ownerId") UUID ownerId);

    @Query("""
            select
                p.id as projectId,
                p.name as projectName,
                p.projectDeadline as projectDeadline,
                s.id as stepId,
                s.name as stepName,
                s.orderIndex as stepOrderIndex,
                s.deadline as stepDeadline,
                count(ss.id) as totalSubsteps,
                coalesce(sum(case when ss.isDone = true then 1 else 0 end), 0) as doneSubsteps
            from Project p
            left join p.steps s
            left join s.substeps ss
            group by p.id, p.name, p.projectDeadline, s.id, s.name, s.orderIndex, s.deadline
            order by p.name asc, s.orderIndex asc
            """)
    List<ProjectStepSummaryRow> findAllProjectStepSummaryRows();

    @Query("""
            select
                p.id as projectId,
                p.name as projectName,
                p.projectDeadline as projectDeadline,
                s.id as stepId,
                s.name as stepName,
                s.orderIndex as stepOrderIndex,
                s.deadline as stepDeadline,
                count(ss.id) as totalSubsteps,
                coalesce(sum(case when ss.isDone = true then 1 else 0 end), 0) as doneSubsteps
            from Project p
            left join p.steps s
            left join s.substeps ss
            where p.owner.id = :ownerId
            group by p.id, p.name, p.projectDeadline, s.id, s.name, s.orderIndex, s.deadline
            order by p.name asc, s.orderIndex asc
            """)
    List<ProjectStepSummaryRow> findProjectStepSummaryRowsByOwnerId(@Param("ownerId") UUID ownerId);

    @Query("""
            select
                p.id as projectId,
                p.name as projectName,
                p.projectDeadline as projectDeadline,

                owner.id as ownerId,
                owner.username as ownerUsername,
                owner.role as ownerRole,

                s.id as stepId,
                s.name as stepName,
                s.orderIndex as stepOrderIndex,
                s.deadline as stepDeadline,

                ss.id as substepId,
                ss.name as substepName,
                ss.orderIndex as substepOrderIndex,
                ss.isDone as substepIsDone
            from Project p
            join p.owner owner
            left join p.steps s
            left join s.substeps ss
            order by p.name asc, s.orderIndex asc, ss.orderIndex asc
            """)
    List<ProjectSummaryFlatRow> findAllProjectSummaryFlatRows();

    @Query("""
            select
                p.id as projectId,
                p.name as projectName,
                p.projectDeadline as projectDeadline,

                owner.id as ownerId,
                owner.username as ownerUsername,
                owner.role as ownerRole,

                s.id as stepId,
                s.name as stepName,
                s.orderIndex as stepOrderIndex,
                s.deadline as stepDeadline,

                ss.id as substepId,
                ss.name as substepName,
                ss.orderIndex as substepOrderIndex,
                ss.isDone as substepIsDone
            from Project p
            join p.owner owner
            left join p.steps s
            left join s.substeps ss
            where p.owner.id = :ownerId
            order by p.name asc, s.orderIndex asc, ss.orderIndex asc
            """)
    List<ProjectSummaryFlatRow> findProjectSummaryFlatRowsByOwnerId(@Param("ownerId") UUID ownerId);
}
