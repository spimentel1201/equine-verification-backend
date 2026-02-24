package com.horsetrust.security;

import com.horsetrust.models.enums.UserRole;

import java.util.UUID;

public interface UserPrincipal {
    UUID getId();
    String getEmail();
    UserRole getRole();
}