package com.vault.securefilevault.repository;

import com.vault.securefilevault.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
    List<AuditLog> findByUsername(String username);
    List<AuditLog> findByAction(String action);
    List<AuditLog> findByFilenameContaining(String filename);

    List<AuditLog> findByActionAndUsername(String action, String username);

    Page<AuditLog> findByAction(String action, Pageable pageable);

}
