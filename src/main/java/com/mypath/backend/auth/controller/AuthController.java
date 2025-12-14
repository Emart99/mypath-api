package com.mypath.backend.auth.controller;

import com.mypath.backend.auth.dto.AuthResponse;
import com.mypath.backend.auth.dto.LoginRequestDTO;
import com.mypath.backend.auth.dto.RegisterRequestDTO;
import com.mypath.backend.auth.service.AuthService;
import com.mypath.backend.auth.dto.RefreshTokenRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequestDTO registerRequest){
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequestDTO request) {
        return ResponseEntity.ok(authService.refresh(request));
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody RefreshTokenRequestDTO request
    ) {
        authService.logout(request);
        return ResponseEntity.ok().build();
    }

}