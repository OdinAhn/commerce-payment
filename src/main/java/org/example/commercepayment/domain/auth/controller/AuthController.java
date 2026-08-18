package org.example.commercepayment.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.commercepayment.domain.auth.dto.AuthResponse;
import org.example.commercepayment.domain.auth.dto.LoginRequest;
import org.example.commercepayment.domain.auth.dto.SignupRequest;
import org.example.commercepayment.domain.auth.service.AuthService;
import org.example.commercepayment.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(
            @Valid @RequestBody SignupRequest request
    ) {

        authService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
