package com.vault.securefilevault.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminOnly(){
        return "Hello Admin";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public String userOnly(){
        return"Hello User";
    }
}
