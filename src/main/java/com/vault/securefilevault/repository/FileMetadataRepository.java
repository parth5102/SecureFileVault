package com.vault.securefilevault.repository;

import com.vault.securefilevault.model.FileMetaData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FileMetadataRepository extends MongoRepository<FileMetaData, String> {
    Optional<FileMetaData> findByKey(String key);
    List<FileMetaData> findByOwnerUsername(String username);
    List<FileMetaData> findBySharedWithContaining(String username);
    void deleteByKey(String key);
}