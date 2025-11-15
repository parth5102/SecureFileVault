package com.vault.securefilevault.controller;

import com.vault.securefilevault.model.FileMetaData;
import com.vault.securefilevault.model.ShareRequest;
import com.vault.securefilevault.repository.FileMetadataRepository;
import com.vault.securefilevault.service.AuditLogService;
import com.vault.securefilevault.service.S3Service;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final S3Service s3Service;
    private final AuditLogService auditLogService;
    private final FileMetadataRepository fileMetadataRepository;
    private final HttpServletRequest request;

    // ✅ UPLOAD FILE
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file,Principal principal) throws Exception {

        String username = principal.getName();
        String key = s3Service.uploadFile(file, username);

        auditLogService.log(
                "UPLOAD",
                username,
                file.getOriginalFilename(),
                "File uploaded successfully"
        );

        Map<String, String> response = new HashMap<>();
        response.put("message", "File uploaded successfully");
        response.put("key", key);

        return ResponseEntity.ok(response);
    }

    // ✅ DOWNLOAD FILE
    @GetMapping("/download/{key:.+}")
    public ResponseEntity<byte[]> download(@PathVariable String key, Principal principal) throws Exception {
        Optional<FileMetaData> optionalMeta = fileMetadataRepository.findByKey(key);
        if (optionalMeta.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        FileMetaData meta = optionalMeta.get();
        String currentUser = principal.getName();

        boolean isOwner = meta.getOwner().equals(currentUser);
        boolean isSharedUser = meta.getSharedWith() != null && meta.getSharedWith().contains(currentUser);

        if (!isOwner && !isSharedUser) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        byte[] decrypted = s3Service.downloadFile(key);
        auditLogService.log("DOWNLOAD", currentUser, key, "Downloaded file");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + meta.getFilename() + "\"")
                .body(decrypted);
    }

    // ✅ SHARE FILE
    @PutMapping("/share")
    public ResponseEntity<String> shareFile(@RequestBody ShareRequest request, Principal principal) {
        s3Service.shareFileWithUser(request.getKey(), principal.getName(), request.getTargetUser());
        auditLogService.log("SHARE", principal.getName(), request.getKey(), "Shared with " + request.getTargetUser());
        return ResponseEntity.ok("File shared with " + request.getTargetUser());
    }

    // ✅ GET FILES SHARED WITH ME
    @GetMapping("/shared-with-me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<FileMetaData> getFilesSharedWithMe(Principal principal) {
        return fileMetadataRepository.findBySharedWithContaining(principal.getName());
    }

    // ✅ GET MY FILES
        @GetMapping("/my-files")
        public ResponseEntity<List<FileMetaData>> listMyFiles(Principal principal) {
            List<FileMetaData> files = fileMetadataRepository.findByOwner(principal.getName());
            return ResponseEntity.ok(files);
        }

    // ✅ DELETE FILE
    @DeleteMapping("/delete/{key}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> deleteFile(@PathVariable String key, Principal principal) {
        Optional<FileMetaData> fileOptional = fileMetadataRepository.findByKey(key);
        if (fileOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
        }

        FileMetaData meta = fileOptional.get();

        if (!meta.getOwner().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to delete this file");
        }

        s3Service.deleteFile(meta.getKey());
        fileMetadataRepository.deleteByKey(key);

        auditLogService.log("DELETE", principal.getName(), key, "File deleted successfully");

        return ResponseEntity.ok("File deleted successfully");
    }

    // ✅ PREVIEW FILE
    @GetMapping("/preview/{key:.+}")
    public ResponseEntity<byte[]> previewFile(@PathVariable String key, Principal principal) throws Exception {
        Optional<FileMetaData> optionalMeta = fileMetadataRepository.findByKey(key);
        if (optionalMeta.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        FileMetaData meta = optionalMeta.get();
        String currentUser = principal.getName();

        boolean isOwner = meta.getOwner().equals(currentUser);
        boolean isSharedUser = meta.getSharedWith() != null && meta.getSharedWith().contains(currentUser);

        if (!isOwner && !isSharedUser) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        byte[] decrypted = s3Service.downloadFile(key);
        String contentType = getContentTypeFromFileName(meta.getFilename());

        auditLogService.log("PREVIEW", currentUser, key, "Previewed file");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + meta.getFilename() + "\"")
                .body(decrypted);
    }

    // ✅ Helper — detect file MIME type
    private String getContentTypeFromFileName(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        return switch (extension) {
            case "pdf" -> "application/pdf";
            case "txt" -> "text/plain";
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            default -> "application/octet-stream";
        };
    }
}
