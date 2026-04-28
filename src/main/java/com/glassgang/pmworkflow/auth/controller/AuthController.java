package com.glassgang.pmworkflow.auth.controller;

import com.glassgang.pmworkflow.auth.dto.LoginRequest;
import com.glassgang.pmworkflow.auth.dto.LoginResponse;
import com.glassgang.pmworkflow.auth.service.AuthService;
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