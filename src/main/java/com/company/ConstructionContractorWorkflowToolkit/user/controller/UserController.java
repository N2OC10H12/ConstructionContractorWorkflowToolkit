package com.company.ConstructionContractorWorkflowToolkit.user.controller;

import com.company.ConstructionContractorWorkflowToolkit.user.dto.CreateUserRequest;
import com.company.ConstructionContractorWorkflowToolkit.user.dto.ChangePasswordRequest;
import com.company.ConstructionContractorWorkflowToolkit.user.dto.AdminResetPasswordRequest;
import com.company.ConstructionContractorWorkflowToolkit.user.dto.UpdateMyProfileRequest;
import com.company.ConstructionContractorWorkflowToolkit.user.dto.UpdateUserRoleRequest;
import com.company.ConstructionContractorWorkflowToolkit.user.dto.UserResponse;
import com.company.ConstructionContractorWorkflowToolkit.user.service.UserService;
import com.company.ConstructionContractorWorkflowToolkit.user.dto.UpdateUserRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse getCurrentUser() {
        return userService.getCurrentUser();
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/project-owners")
    public List<UserResponse> getProjectOwnerCandidates() {
        return userService.getProjectOwnerCandidates();
    }

    @PostMapping
    public UserResponse createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @PatchMapping("/{userId}/role")
    public UserResponse updateUserRole(@PathVariable UUID userId,
            @RequestBody UpdateUserRoleRequest request) {
        return userService.updateUserRole(userId, request);
    }

    @PatchMapping("/me")
    public UserResponse updateMyProfile(@RequestBody UpdateMyProfileRequest request) {
        return userService.updateMyProfile(request);
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> changeMyPassword(@RequestBody ChangePasswordRequest request) {
        userService.changeMyPassword(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}")
    public UserResponse updateUser(
            @PathVariable UUID userId,
            @RequestBody UpdateUserRequest request) {
        return userService.updateUser(userId, request);
    }

    @PatchMapping("/{userId}/password")
    public ResponseEntity<Void> resetUserPassword(
            @PathVariable UUID userId,
            @RequestBody AdminResetPasswordRequest request) {
        userService.resetUserPassword(userId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
    }
}