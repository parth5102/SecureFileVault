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

    @GetMapping("/my-downloads")
    public List<AuditLog> getMyDownloads(Principal principal){
        return auditLogRepository.findByUsername(principal.getName())
                .stream()
                .filter(log -> "DOWNLOAD".equals(log.getAction()))
                .toList();
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<AuditLog> getFilteredLogs(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String filename
    ){
        if(action != null && username != null) {
            return auditLogRepository.findByActionAndUsername(action, username);
        } else if (action != null) {
            return auditLogRepository.findByAction(action);
        } else if (username != null) {
            return auditLogRepository.findByUsername(username);
        } else if (filename != null) {
            return auditLogRepository.findByFilenameContaining(filename);
        }else {
            return auditLogRepository.findAll();
        }
    }

    @GetMapping("/filter-page")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<AuditLog> getFilteredLogsPaged(
            @RequestParam(required = false) String action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        if (action!= null){
            return auditLogRepository.findByAction(action, pageable);
        }
        else {
            return auditLogRepository.findAll(pageable);
        }
    }
}
