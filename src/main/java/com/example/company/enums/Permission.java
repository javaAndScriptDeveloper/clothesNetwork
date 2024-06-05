package com.example.company.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Permission implements GrantedAuthority {
    BO_READ,
    BO_WRITE;

    @Override
    public String getAuthority() {
        return name();
    }
}
