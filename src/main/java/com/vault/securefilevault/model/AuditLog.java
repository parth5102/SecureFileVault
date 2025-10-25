package com.vault.securefilevault.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("audit_logs")
public class AuditLog {
    @Id
    private String id;

    private String action;
    private String username;
    private String target;
    private String ip;
    private Instant at;
    private String details;
}
