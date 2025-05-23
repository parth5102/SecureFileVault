package com.vault.securefilevault.controller;

import com.vault.securefilevault.model.AuditLog;
import com.vault.securefilevault.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class AuditLogController {

    @Autowired
    AuditLogRepository auditLogRepository;

    @GetMapping("/all")
    public List<AuditLog> getAllLogs(){
        return auditLogRepository.findAll();
    }
}
