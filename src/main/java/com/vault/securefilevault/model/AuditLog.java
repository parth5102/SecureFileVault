package com.vault.securefilevault.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "audit_log")
public class AuditLog {

    @Id private String id;
    private String username;
    private String action;
    private String filename;
    private Date timestamp = new Date();

    public AuditLog(String id, String username, String action, String filename, Date timestamp) {
        this.id = id;
        this.username = username;
        this.action = action;
        this.filename = filename;
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", action='" + action + '\'' +
                ", filename='" + filename + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
