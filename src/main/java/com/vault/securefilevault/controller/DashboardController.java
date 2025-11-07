package com.vault.securefilevault.controller;

import com.vault.securefilevault.model.AuditLog;
import com.vault.securefilevault.model.FileMetaData;
import com.vault.securefilevault.repository.AuditLogRepository;
import com.vault.securefilevault.repository.FileMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private FileMetadataRepository fileRepo;

    @Autowired
    private AuditLogRepository auditRepo;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserDashboard(Principal principal){
        String userName = principal.getName();

        List<FileMetaData> myFiles = fileRepo.findByOwner(userName);
        List<FileMetaData> sharedFiles = fileRepo.findBySharedWithContaining(userName);
        List<AuditLog> myLogs = auditRepo.findByUserName(userName);

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("username", userName);
        dashboard.put("totalFiles", myFiles.size());
        dashboard.put("sharedFilesCount", sharedFiles.size());
        dashboard.put("myFiles", myFiles);
        dashboard.put("sharedWithMe", sharedFiles);
        dashboard.put("recentActivity", myLogs.stream().limit(10).toList());

        return ResponseEntity.ok(dashboard);
    }
}