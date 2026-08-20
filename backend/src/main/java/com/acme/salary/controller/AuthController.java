package com.acme.salary.controller;

import com.acme.salary.dto.LoginRequest;
import com.acme.salary.dto.LoginResponse;
import com.acme.salary.service.AuthService;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;

import java.util.Map;
import java.util.Optional;

@Controller("/api/auth")
public class AuthController {

    @Inject
    private AuthService authService;

    @Post("/login")
    public HttpResponse<?> login(@Body LoginRequest request, HttpRequest<?> httpRequest) {
        String ipAddress = httpRequest.getRemoteAddress().getAddress().getHostAddress();
        
        Optional<LoginResponse> response = authService.login(request, ipAddress);
        
        if (response.isPresent()) {
            return HttpResponse.ok(response.get());
        } else {
            return HttpResponse.unauthorized().body(Map.of("message", "Invalid username or password"));
        }
    }

    @Post("/logout")
    public HttpResponse<?> logout(@Body Map<String, Object> payload, HttpRequest<?> httpRequest) {
        String ipAddress = httpRequest.getRemoteAddress().getAddress().getHostAddress();
        Long userId = payload.get("userId") != null ? ((Number) payload.get("userId")).longValue() : null;
        String username = (String) payload.get("username");
        
        if (userId != null && username != null) {
            authService.logout(userId, username, ipAddress);
        }
        
        return HttpResponse.ok(Map.of("message", "Logged out successfully"));
    }

    @Get("/validate")
    public HttpResponse<?> validateToken() {
        // If this endpoint is reached, token is valid (will be protected by security filter)
        return HttpResponse.ok(Map.of("valid", true));
    }
}
