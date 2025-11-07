package com.vault.securefilevault.service;

import com.vault.securefilevault.Util.AESUtil;
import com.vault.securefilevault.model.FileMetaData;
import com.vault.securefilevault.repository.FileMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Service
public class S3Service {

    private final FileMetadataRepository fileRepo;
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    // Custom constructor to initialize S3Client with region
    public S3Service(@Value("${aws.region}") String region,
                     FileMetadataRepository fileRepo) {
        this.fileRepo = fileRepo;
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    public String uploadFile(MultipartFile file, String username) throws Exception {
        byte[] encrypted = AESUtil.encrypt(file.getBytes());
        String key = UUID.randomUUID() + "-" + file.getOriginalFilename();

        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(detectContentType(file.getOriginalFilename()))
                .build();

        s3Client.putObject(putReq, RequestBody.fromBytes(encrypted));

        FileMetaData meta = FileMetaData.builder()
                .key(key)
                .owner(username)
                .filename(file.getOriginalFilename())
                .contentType(detectContentType(file.getOriginalFilename()))
                .sizeBytes(file.getSize())
                .uploadedAt(Instant.now())
                .sharedWith(new HashSet<>())
                .build();

        fileRepo.save(meta);

        return "File uploaded successfully with key: " + key;
    }

    public byte[] downloadFile(String key) throws Exception {
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        ResponseBytes<GetObjectResponse> s3Object = s3Client.getObjectAsBytes(getReq);
        return AESUtil.decrypt(s3Object.asByteArray());
    }

    public void deleteFile(String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());
        fileRepo.findByKey(key).ifPresent(fileRepo::delete);
    }

    public void shareFileWithUser(String key, String owner, String targetUser) {
        Optional<FileMetaData> optMeta = fileRepo.findByKey(key);
        if (optMeta.isEmpty()) throw new RuntimeException("File not found");

        FileMetaData meta = optMeta.get();
        if (!meta.getOwner().equals(owner))
            throw new RuntimeException("You are not the owner of this file");

        if (!meta.getSharedWith().contains(targetUser)) {
            meta.getSharedWith().add(targetUser);
            fileRepo.save(meta);
        }
    }

    private String detectContentType(String filename) {
        String ext = filename.contains(".") ? filename.substring(filename.lastIndexOf('.') + 1).toLowerCase() : "";
        return switch (ext) {
            case "pdf" -> "application/pdf";
            case "txt" -> "text/plain";
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            default -> "application/octet-stream";
        };
    }
}
