package com.sacret.note.controller;

import com.sacret.note.model.request.RegistrationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sacret.note.model.request.AuthRequest;
import com.sacret.note.model.request.RefreshTokenRequest;
import com.sacret.note.model.response.AuthResponse;
import com.sacret.note.service.UserService;

@Tag(name = "Auth API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/users")
@SecurityRequirement(name = "note")
public class AuthController {

    private final UserService userService;

    @PostMapping("/registrations")
    @Operation(description = "Register user")
    public AuthResponse registerUser(@RequestBody @Valid RegistrationRequest request) {
        return userService.register(request);
    }

    /**
     * The following methods are used only to display the models in the swagger,
     * the implementation of the login and refresh token is in the security filters
     */

    @PostMapping("/auth")
    @Operation(description = "User authentication")
    public AuthResponse auth(@RequestBody AuthRequest request) {
        return null;
    }

    @PostMapping("/auth/refresh-tokens")
    @Operation(description = "Refresh token")
    public AuthResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return null;
    }
}
