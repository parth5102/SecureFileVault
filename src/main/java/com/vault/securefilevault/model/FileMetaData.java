package com.vault.securefilevault.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Document(collection = "files")
public class FileMetaData {

    @Id
    private String id;

    private String key;           // S3 object key
    private String owner;         // username
    private String filename;      // original filename
    private long sizeBytes;
    private String contentType;   // detected on upload
    private Instant uploadedAt;

    @Builder.Default
    private Set<String> sharedWith = new HashSet<>();


}
