package com.glassgang.pmworkflow.auth.service;

import com.glassgang.pmworkflow.auth.dto.LoginRequest;
import com.glassgang.pmworkflow.auth.dto.LoginResponse;
import com.glassgang.pmworkflow.common.exception.BadRequestException;
import com.glassgang.pmworkflow.user.entity.AppUser;
import com.glassgang.pmworkflow.user.repository.AppUserRepository;
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