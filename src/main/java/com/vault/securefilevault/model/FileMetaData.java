package com.vault.securefilevault.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "files")
public class FileMetaData {

    @Id
    private String id;
    private String key;
    private String originalFilename;
    private String ownerUsername;
    private Date uploadAt = new Date();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public Date getUploadAt() {
        return uploadAt;
    }

    public void setUploadAt(Date uploadAt) {
        this.uploadAt = uploadAt;
    }

    @Override
    public String toString() {
        return "FileMetaData{" +
                "id='" + id + '\'' +
                ", key='" + key + '\'' +
                ", originalFilename='" + originalFilename + '\'' +
                ", ownerUsername='" + ownerUsername + '\'' +
                ", uploadAt=" + uploadAt +
                '}';
    }
}
