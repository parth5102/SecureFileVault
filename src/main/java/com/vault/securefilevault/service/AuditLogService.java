package com.vault.securefilevault.service;

import com.vault.securefilevault.model.AuditLog;
import com.vault.securefilevault.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void log(String username, String action, String filename, String ipAddress){
        AuditLog log = new AuditLog();
        log.setUsername(username);
        log.setAction(action);
        log.setFilename(filename);
        log.setIpAddress(ipAddress);
        log.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(log);
    }
}
