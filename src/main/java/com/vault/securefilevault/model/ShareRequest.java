package com.vault.securefilevault.model;

import lombok.Data;

@Data
public class ShareRequest {

    private String key;
    private String targetUser;

}
