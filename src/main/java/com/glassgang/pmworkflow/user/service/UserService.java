package com.glassgang.pmworkflow.user.service;

import com.glassgang.pmworkflow.common.exception.BadRequestException;
import com.glassgang.pmworkflow.common.exception.ForbiddenException;
import com.glassgang.pmworkflow.common.exception.NotFoundException;
import com.glassgang.pmworkflow.common.util.CurrentUserUtil;
import com.glassgang.pmworkflow.user.dto.CreateUserRequest;
import com.glassgang.pmworkflow.user.dto.UpdateUserRoleRequest;
import com.glassgang.pmworkflow.user.dto.UserResponse;
import com.glassgang.pmworkflow.user.entity.AppUser;
import com.glassgang.pmworkflow.user.entity.Role;
import com.glassgang.pmworkflow.user.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AppUserRepository appUserRepository;
    private final CurrentUserUtil currentUserUtil;
    private final PasswordEncoder passwordEncoder;

    public UserResponse getCurrentUser() {
        return toResponse(currentUserUtil.getCurrentUser());
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

        AppUser user = new AppUser();
        user.setId(UUID.randomUUID());
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role.name());
        user.setCreatedAt(LocalDateTime.now());

        return toResponse(appUserRepository.save(user));
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
                user.getRole(),
                user.getCreatedAt()
        );
    }
}