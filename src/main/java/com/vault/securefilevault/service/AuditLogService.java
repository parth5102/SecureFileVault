package com.vault.securefilevault.service;

import com.vault.securefilevault.model.AuditLog;
import com.vault.securefilevault.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    private final AuditLogRepository repo;
    private final HttpServletRequest request;

    public AuditLogService(AuditLogRepository repo, HttpServletRequest request) {
        this.repo = repo;
        this.request = request;
    }

    public void log(String action, String username, String target, String details) {
        String ip = request.getRemoteAddr(); // IPv6 (::1) kept
        repo.save(AuditLog.builder()
                .action(action)
                .username(username)
                .target(target)
                .ip(ip)
                .at(Instant.now())
                .details(details)
                .build());
    }
}
