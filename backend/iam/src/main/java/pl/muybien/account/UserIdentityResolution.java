package pl.muybien.account;

public record UserIdentityResolution(
        String username,
        String email,
        boolean emailVerified
) {}
