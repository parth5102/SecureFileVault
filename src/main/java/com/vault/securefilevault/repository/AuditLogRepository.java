package com.vault.securefilevault.repository;

import com.vault.securefilevault.model.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
    List<AuditLog> findByUsername(String username);
}
