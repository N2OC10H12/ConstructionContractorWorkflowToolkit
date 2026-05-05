package com.glassgang.pmworkflow.user.service;

import com.glassgang.pmworkflow.common.exception.BadRequestException;
import com.glassgang.pmworkflow.user.dto.UpdateUserRequest;
import com.glassgang.pmworkflow.user.dto.ChangePasswordRequest;
import com.glassgang.pmworkflow.user.dto.UpdateMyProfileRequest;
import com.glassgang.pmworkflow.common.exception.ForbiddenException;
import com.glassgang.pmworkflow.common.exception.NotFoundException;
import com.glassgang.pmworkflow.common.util.CurrentUserUtil;
import com.glassgang.pmworkflow.user.dto.CreateUserRequest;
import com.glassgang.pmworkflow.user.dto.UpdateUserRoleRequest;
import com.glassgang.pmworkflow.user.dto.UserResponse;
import com.glassgang.pmworkflow.user.entity.AppUser;
import com.glassgang.pmworkflow.user.entity.Role;
import com.glassgang.pmworkflow.user.repository.AppUserRepository;
import com.glassgang.pmworkflow.user.dto.AdminResetPasswordRequest;
import com.glassgang.pmworkflow.project.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AppUserRepository appUserRepository;
    private final CurrentUserUtil currentUserUtil;
    private final PasswordEncoder passwordEncoder;
    private final ProjectRepository projectRepository;

    public UserResponse getCurrentUser() {
        UUID currentUserId = currentUserUtil.getCurrentUserId();

        AppUser user = appUserRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("Current user not found"));

        return toResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        requireAdmin();

        return appUserRepository.findAllByOrderByUsernameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<UserResponse> getProjectOwnerCandidates() {
        requireAdmin();

        return appUserRepository.findByRoleOrderByUsernameAsc(Role.PM.name())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse createUser(CreateUserRequest request) {
        requireAdmin();

        String username = normalizeUsername(request.getUsername());
        String password = request.getPassword();
        Role role = parseRole(request.getRole());

        if (password == null || password.isBlank()) {
            throw new BadRequestException("Password is required");
        }

        if (password.length() < 4) {
            throw new BadRequestException("Password must be at least 4 characters");
        }

        if (appUserRepository.findByUsername(username).isPresent()) {
            throw new BadRequestException("Username already exists");
        }

        String displayName = request.getDisplayName();

        if (displayName == null || displayName.isBlank()) {
            throw new BadRequestException("Display name is required");
        }

        displayName = displayName.trim();

        if (displayName.length() > 150) {
            throw new BadRequestException("Display name cannot exceed 150 characters");
        }

        AppUser user = new AppUser();
        user.setId(UUID.randomUUID());
        user.setUsername(username);
        user.setDisplayName(displayName); // ✅ NEW
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role.name());
        user.setCreatedAt(LocalDateTime.now());

        return toResponse(appUserRepository.save(user));
    }

    @Transactional
    public void deleteUser(UUID userId) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        UUID currentUserId = currentUserUtil.getCurrentUserId();

        if (user.getId().equals(currentUserId)) {
            throw new BadRequestException("You cannot delete your own account");
        }

        if (projectRepository.existsByOwner_Id(userId)) {
            throw new BadRequestException("Cannot delete user because they own one or more projects");
        }

        appUserRepository.delete(user);
    }

    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
        requireAdmin();

        AppUser targetUser = appUserRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        String username = normalizeUsername(request.getUsername());

        String displayName = request.getDisplayName();
        if (displayName == null || displayName.isBlank()) {
            throw new BadRequestException("Display name is required");
        }

        displayName = displayName.trim();

        if (displayName.length() > 150) {
            throw new BadRequestException("Display name cannot exceed 150 characters");
        }

        Role role = parseRole(request.getRole());

        appUserRepository.findByUsername(username)
                .filter(existing -> !existing.getId().equals(userId))
                .ifPresent(existing -> {
                    throw new BadRequestException("Username already exists");
                });

        targetUser.setUsername(username);
        targetUser.setDisplayName(displayName);
        targetUser.setRole(role.name());

        return toResponse(appUserRepository.save(targetUser));
    }

    public UserResponse updateUserRole(UUID userId, UpdateUserRoleRequest request) {
        requireAdmin();

        Role newRole = parseRole(request.getRole());

        AppUser targetUser = appUserRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (targetUser.getId().equals(currentUserUtil.getCurrentUserId())) {
            throw new BadRequestException("You cannot change your own role");
        }

        targetUser.setRole(newRole.name());

        return toResponse(appUserRepository.save(targetUser));
    }

    public UserResponse updateMyProfile(UpdateMyProfileRequest request) {
        UUID currentUserId = currentUserUtil.getCurrentUserId();

        AppUser user = appUserRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("Current user not found"));

        String displayName = request.getDisplayName();

        if (displayName == null || displayName.isBlank()) {
            throw new BadRequestException("Display name is required");
        }

        displayName = displayName.trim();

        if (displayName.length() > 150) {
            throw new BadRequestException("Display name cannot exceed 150 characters");
        }

        user.setDisplayName(displayName);

        return toResponse(appUserRepository.save(user));
    }

    public void changeMyPassword(ChangePasswordRequest request) {
        UUID currentUserId = currentUserUtil.getCurrentUserId();

        AppUser user = appUserRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("Current user not found"));

        if (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank()) {
            throw new BadRequestException("Current password is required");
        }

        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            throw new BadRequestException("New password is required");
        }

        if (request.getNewPassword().length() < 4) {
            throw new BadRequestException("New password must be at least 4 characters");
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));

        appUserRepository.save(user);
    }

    public void resetUserPassword(UUID userId, AdminResetPasswordRequest request) {
        requireAdmin();

        AppUser targetUser = appUserRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            throw new BadRequestException("New password is required");
        }

        if (request.getNewPassword().length() < 4) {
            throw new BadRequestException("New password must be at least 4 characters");
        }

        targetUser.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));

        appUserRepository.save(targetUser);
    }

    private void requireAdmin() {
        if (!currentUserUtil.isCurrentUserAdmin()) {
            throw new ForbiddenException("Admin access required");
        }
    }

    private String normalizeUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new BadRequestException("Username is required");
        }

        return username.trim();
    }

    private Role parseRole(String role) {
        try {
            return Role.from(role);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }

    private UserResponse toResponse(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getRole(),
                user.getCreatedAt());
    }
}