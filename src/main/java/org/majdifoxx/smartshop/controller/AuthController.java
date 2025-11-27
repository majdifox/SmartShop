package org.majdifoxx.smartshop.controller;

import lombok.RequiredArgsConstructor;
import org.majdifoxx.smartshop.dto.request.LoginRequestDTO;
import org.majdifoxx.smartshop.dto.response.LoginResponseDTO;
import org.majdifoxx.smartshop.entity.User;
import org.majdifoxx.smartshop.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Authentication endpoints
 * PDF: "Authentification par HTTP Session (login/logout)"
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/login
     * PDF: "login/logout"
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/auth/logout
     * PDF: "login/logout"
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        authService.logout();
        return ResponseEntity.ok("Logout successful");
    }

    /**
     * GET /api/auth/me
     * Gets current logged-in user info
     */
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(user);
    }
}
