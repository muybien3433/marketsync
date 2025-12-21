package pl.muybien.dto.iam.response;

public record KeycloakEmailChangedResponse(
        String userId,
        String username,
        String email,
        boolean emailVerified
) {
}
