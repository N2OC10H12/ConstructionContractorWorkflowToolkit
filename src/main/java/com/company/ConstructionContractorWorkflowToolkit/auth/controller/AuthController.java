package com.company.ConstructionContractorWorkflowToolkit.auth.controller;

import com.company.ConstructionContractorWorkflowToolkit.auth.dto.LoginRequest;
import com.company.ConstructionContractorWorkflowToolkit.auth.dto.LoginResponse;
import com.company.ConstructionContractorWorkflowToolkit.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}