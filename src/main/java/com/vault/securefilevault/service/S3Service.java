package com.vault.securefilevault.service;

import com.vault.securefilevault.Util.AESUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Service
public class S3Service {
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    public S3Service(@Value("${aws.region}") String region){
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    public String uploadFile(MultipartFile file) throws Exception {
        byte[] encrypted = AESUtil.encrypt(file.getBytes());

        String key = UUID.randomUUID() + "-" + file.getOriginalFilename();

        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.putObject(putReq, RequestBody.fromBytes(encrypted));
        return "File uploaded as: " + key ;
    }

    public byte[] downloadFile(String key) throws Exception{
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        ResponseBytes<GetObjectResponse> s3Object = s3Client.getObjectAsBytes(getReq);
        return AESUtil.decrypt(s3Object.asByteArray());
    }

}
