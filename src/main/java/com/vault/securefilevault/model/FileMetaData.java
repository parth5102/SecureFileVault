package com.vault.securefilevault.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "files")
public class FileMetaData {

    @Id
    private String id;
    private String key;
    private String originalFilename;
    private String ownerUsername;
    private Date uploadAt = new Date();
    private List<String> sharedWith = new ArrayList<>();

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

    public List<String> getSharedWith() {
        return sharedWith;
    }

    public void setSharedWith(List<String> sharedWith) {
        this.sharedWith = sharedWith;
    }


    @Override
    public String toString() {
        return "FileMetaData{" +
                "id='" + id + '\'' +
                ", key='" + key + '\'' +
                ", originalFilename='" + originalFilename + '\'' +
                ", ownerUsername='" + ownerUsername + '\'' +
                ", uploadAt=" + uploadAt + '\'' +
                ", sharedWith=" + sharedWith +
                '}';
    }
}
