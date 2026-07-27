package com.company.ConstructionContractorWorkflowToolkit.user.repository;

import com.company.ConstructionContractorWorkflowToolkit.user.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    Optional<AppUser> findByUsername(String username);

    List<AppUser> findAllByOrderByUsernameAsc();

    List<AppUser> findByRoleOrderByUsernameAsc(String role);

    List<AppUser> findByRoleInOrderByUsernameAsc(List<String> roles);
}