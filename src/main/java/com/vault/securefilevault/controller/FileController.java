package com.vault.securefilevault.controller;

import com.vault.securefilevault.model.AuditLog;
import com.vault.securefilevault.model.FileMetaData;
import com.vault.securefilevault.model.ShareRequest;
import com.vault.securefilevault.repository.AuditLogRepository;
import com.vault.securefilevault.repository.FileMetadataRepository;
import com.vault.securefilevault.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    S3Service s3Service;

    @Autowired
    AuditLogRepository auditLogRepository;

    @Autowired
    FileMetadataRepository fileMetadataRepository;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file")MultipartFile file, Principal principal) throws Exception{
        String currentUsername = principal.getName();
        return ResponseEntity.ok(s3Service.uploadFile(file, currentUsername));
    }   

    @GetMapping("/download/{key:.+}")
    public ResponseEntity<byte[]> download(@PathVariable String key, Principal principal) throws Exception {
        System.out.println("🔍 Download requested for key: " + key);
        System.out.println("👤 Principal: " + (principal != null ? principal.getName() : "null"));

        Optional<FileMetaData> optionalMeta = fileMetadataRepository.findByKey(key);

        if (optionalMeta.isEmpty()) {
            System.out.println("❌ FileMetaData not found for key: " + key);
            return ResponseEntity.notFound().build();
        }

        FileMetaData meta = optionalMeta.get();
        System.out.println("📄 FileMetaData found: " + meta);

        String currentUser = principal.getName();
        boolean isOwner = meta.getOwnerUsername().equals(currentUser);
        List<String> sharedWith = meta.getSharedWith() != null ? meta.getSharedWith() : List.of();
        boolean isSharedUser = sharedWith.contains(currentUser);

        System.out.println("🧾 isOwner: " + isOwner);
        System.out.println("🧾 isSharedUser: " + isSharedUser);

        if (!isOwner && !isSharedUser) {
            System.out.println("⛔ Access denied for user: " + currentUser);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        byte[] decrypted = s3Service.downloadFile(key);

        AuditLog log = new AuditLog();
        log.setUsername(currentUser);
        log.setAction("DOWNLOAD");
        log.setFilename(key);
        auditLogRepository.save(log);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + key + "\"")
                .body(decrypted);
    }

    @PutMapping("/share")
    public ResponseEntity<String> shareFile(@RequestBody ShareRequest request, Principal principal){
        s3Service.shareFileWithUser(request.getKey(), principal.getName(),request.getTargetUser());
        return ResponseEntity.ok("File shared with " + request.getTargetUser());
    }

}
