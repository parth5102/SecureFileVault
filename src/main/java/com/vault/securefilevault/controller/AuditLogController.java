package com.vault.securefilevault.controller;

import com.vault.securefilevault.model.AuditLog;
import com.vault.securefilevault.model.User;
import com.vault.securefilevault.repository.AuditLogRepository;
import com.vault.securefilevault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class AuditLogController {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Fetch all logs for ADMINs, or user-specific logs for regular users.
     */
    @GetMapping("/all")
    public ResponseEntity<List<AuditLog>> getLogs(Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.ok(auditLogRepository.findAll());
        } else {
            return ResponseEntity.ok(auditLogRepository.findByUserName(username));
        }
    }

    /**
     * ADMIN: Get logs for a specific user.
     */
    @GetMapping("/user/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLog>> getLogsByUser(@PathVariable String username) {
        List<AuditLog> logs = auditLogRepository.findByUserName(username);
        return ResponseEntity.ok(logs);
    }

    /**
     * USER: Get only your own DOWNLOAD logs.
     */
    @GetMapping("/my-downloads")
    public ResponseEntity<List<AuditLog>> getMyDownloads(Principal principal) {
        List<AuditLog> downloads = auditLogRepository.findByUserName(principal.getName())
                .stream()
                .filter(log -> "DOWNLOAD".equalsIgnoreCase(log.getAction()))
                .toList();
        return ResponseEntity.ok(downloads);
    }

    /**
     * ADMIN: Filter logs dynamically by action, username, or filename.
     */
    @GetMapping("/filter")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLog>> getFilteredLogs(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String filename
    ) {
        if (action != null && username != null) {
            return ResponseEntity.ok(auditLogRepository.findByActionAndUserName(action, username));
        } else if (action != null) {
            return ResponseEntity.ok(auditLogRepository.findByAction(action));
        } else if (username != null) {
            return ResponseEntity.ok(auditLogRepository.findByUserName(username));
        } else if (filename != null) {
            return ResponseEntity.ok(auditLogRepository.findByFileNameContaining(filename));
        } else {
            return ResponseEntity.ok(auditLogRepository.findAll());
        }
    }

    /**
     * ADMIN: Filter logs with pagination.
     */
    @GetMapping("/filter-page")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditLog>> getFilteredLogsPaged(
            @RequestParam(required = false) String action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        if (action != null) {
            return ResponseEntity.ok(auditLogRepository.findByAction(action, pageable));
        } else {
            return ResponseEntity.ok(auditLogRepository.findAll(pageable));
        }
    }
}
