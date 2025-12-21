package pl.muybien.dto.iam.response;

public record KeycloakUserCreatedResponse(
        String id,
        String username
) {
}