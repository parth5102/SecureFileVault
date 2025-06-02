package com.vault.securefilevault.controller;

import com.vault.securefilevault.model.AuditLog;
import com.vault.securefilevault.model.User;
import com.vault.securefilevault.repository.AuditLogRepository;
import com.vault.securefilevault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class AuditLogController {

    @Autowired
    AuditLogRepository auditLogRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/all")
    public List<AuditLog> getLogs(Principal principal){
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        if ("ADMIN" .equalsIgnoreCase(user.getRole())){
            return auditLogRepository.findAll();
        }
        else {
            return auditLogRepository.findByUsername(username);
        }
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLog>> getLogsByUser(@PathVariable String username){
        List<AuditLog> logs = auditLogRepository.findByUsername(username);
        return ResponseEntity.ok(logs);
    }
}
