package com.vault.securefilevault.repository;

import com.vault.securefilevault.model.FileMetaData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FileMetadataRepository extends MongoRepository<FileMetaData, String> {
    Optional<FileMetaData> findByKey(String key);

}