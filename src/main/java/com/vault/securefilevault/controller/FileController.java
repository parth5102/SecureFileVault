package com.vault.securefilevault.controller;

import com.vault.securefilevault.model.AuditLog;
import com.vault.securefilevault.model.FileMetaData;
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

    @GetMapping("/download/{key}")
    public ResponseEntity<byte[]> download(@PathVariable String key, Principal principal) throws Exception {

        Optional<FileMetaData> optionalMeta = fileMetadataRepository.findByKey(key);

        if (optionalMeta.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        FileMetaData meta = optionalMeta.get();

        boolean isOwner = meta.getOwnerUsername().equals(principal.getName());
        boolean isSharedUser = meta.getSharedWith().contains(principal.getName());

        if (!isOwner & !isSharedUser){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }


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

    @PutMapping("/share/{key}")
    public ResponseEntity<String> shareFile(@PathVariable String key, @RequestParam String targetUser, Principal principal){
        s3Service.shareFileWithUser(key, principal.getName(), targetUser);
        return ResponseEntity.ok("File shared with " + targetUser);
    }

}
