package com.vault.securefilevault.controller;

import com.vault.securefilevault.model.AuditLog;
import com.vault.securefilevault.model.FileMetaData;
import com.vault.securefilevault.repository.AuditLogRepository;
import com.vault.securefilevault.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    S3Service s3Service;

    @Autowired
    AuditLogRepository auditLogRepository;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file")MultipartFile file, Principal principal) throws Exception{
        String currentUsername = principal.getName();
        return ResponseEntity.ok(s3Service.uploadFile(file, currentUsername));
    }

    @GetMapping("/download/{key}")
    public ResponseEntity<byte[]> download(@PathVariable String key, Principal principal) throws Exception {
        byte[] decrypted = s3Service.downloadFile(key);

        AuditLog log = new AuditLog();
        log.setUsername(principal.getName());
        log.setAction("DOWNLOAD");
        log.setFilename(key);
        auditLogRepository.save(log);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + key + "\"")
                .body(decrypted);
    }

}
