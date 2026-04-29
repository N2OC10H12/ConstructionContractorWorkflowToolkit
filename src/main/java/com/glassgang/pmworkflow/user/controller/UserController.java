package com.glassgang.pmworkflow.user.controller;

import com.glassgang.pmworkflow.user.dto.CreateUserRequest;
import com.glassgang.pmworkflow.user.dto.UpdateUserRoleRequest;
import com.glassgang.pmworkflow.user.dto.UserResponse;
import com.glassgang.pmworkflow.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}