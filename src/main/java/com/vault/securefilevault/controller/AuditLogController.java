package com.vault.securefilevault.controller;

import com.vault.securefilevault.model.AuditLog;
import com.vault.securefilevault.model.User;
import com.vault.securefilevault.repository.AuditLogRepository;
import com.vault.securefilevault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
}
