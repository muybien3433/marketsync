package pl.muybien.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "internal-jwt")
public record InternalJwtProperties(
        String issuer,
        String audience,
        Long ttlSeconds,
        String keyId,
        String privateKeyPem,
        String publicKeyPem
) {}
