package com.vault.securefilevault.controller;

import com.vault.securefilevault.model.User;
import com.vault.securefilevault.service.AuditLogService;
import com.vault.securefilevault.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuditLogService auditLogService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody User user) {
        String message = authService.registerUser(user);
        auditLogService.log("REGISTER", user.getUsername(), null, "User registered");
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
        String token = authService.loginUser(user);
        auditLogService.log("LOGIN", user.getUsername(), null, "User logged in");
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/register-admin")
    public ResponseEntity<Map<String, String>> registerAdmin(@RequestBody User user) {
        user.setRole("ADMIN");
        String message = authService.registerUser(user);
        auditLogService.log("REGISTER", user.getUsername(), null, "Admin registered");
        return ResponseEntity.ok(Map.of("message", message));
    }
}
