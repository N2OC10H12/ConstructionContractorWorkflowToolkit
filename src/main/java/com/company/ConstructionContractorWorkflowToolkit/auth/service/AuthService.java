package com.company.ConstructionContractorWorkflowToolkit.auth.service;

import com.company.ConstructionContractorWorkflowToolkit.auth.dto.LoginRequest;
import com.company.ConstructionContractorWorkflowToolkit.auth.dto.LoginResponse;
import com.company.ConstructionContractorWorkflowToolkit.common.exception.BadRequestException;
import com.company.ConstructionContractorWorkflowToolkit.user.entity.AppUser;
import com.company.ConstructionContractorWorkflowToolkit.user.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {

        AppUser user = appUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("USER NOT FOUND"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("PASSWORD DOES NOT MATCH");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                token,
                "Bearer",
                user.getUsername(),
                user.getRole()
        );
    }
}