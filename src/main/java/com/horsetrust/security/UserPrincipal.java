package com.horsetrust.security;

import java.util.UUID;

public interface UserPrincipal {
    UUID getId();
    String getRole();
}