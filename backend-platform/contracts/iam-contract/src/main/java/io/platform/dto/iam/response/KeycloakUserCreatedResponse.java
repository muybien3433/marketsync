package io.platform.dto.iam.response;

public record KeycloakUserCreatedResponse(
        String id,
        String username
) {
}