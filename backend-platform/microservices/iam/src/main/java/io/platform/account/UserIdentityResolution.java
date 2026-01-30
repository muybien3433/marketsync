package io.platform.account;

public record UserIdentityResolution(
        String username,
        String email,
        boolean emailVerified
) {}
