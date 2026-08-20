package com.acme.salary.service;

import com.acme.salary.dto.LoginRequest;
import com.acme.salary.dto.LoginResponse;
import com.acme.salary.model.User;
import com.acme.salary.repository.UserRepository;
import io.micronaut.security.token.generator.TokenGenerator;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Singleton
public class AuthService {

    @Inject
    private UserRepository userRepository;

    @Inject
    private TokenGenerator tokenGenerator;

    @Inject
    private AuditService auditService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Optional<LoginResponse> login(LoginRequest request, String ipAddress) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();

        if (!user.getActive()) {
            return Optional.empty();
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return Optional.empty();
        }

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.update(user);

        // Generate JWT token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole());

        String token = tokenGenerator.generateToken(claims).orElse("");

        // Log audit
        auditService.logAction("LOGIN", "User", user.getId(), user.getId(), user.getUsername(), null, null, ipAddress);

        return Optional.of(new LoginResponse(token, user.getUsername(), user.getEmail(), user.getRole(), user.getId()));
    }

    public User registerUser(String username, String email, String password, String role) {
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(username, email, hashedPassword, role);
        return userRepository.save(user);
    }

    public void logout(Long userId, String username, String ipAddress) {
        auditService.logAction("LOGOUT", "User", userId, userId, username, null, null, ipAddress);
    }
}
