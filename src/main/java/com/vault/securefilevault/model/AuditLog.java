package com.vault.securefilevault.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;

@Document(collection = "audit_log")
public class AuditLog {

    @Id private String id;
    private String username;
    private String action;
    private String filename;
    private String ipAddress;
    private LocalDateTime timestamp;

    public AuditLog(String id, String username, String action, String filename, String ipAddress, LocalDateTime timestamp) {
        this.id = id;
        this.username = username;
        this.action = action;
        this.filename = filename;
        this.ipAddress = ipAddress;
        this.timestamp = timestamp;
    }

    public AuditLog() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", action='" + action + '\'' +
                ", filename='" + filename + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
