package pl.muybien.dto.iam.response;

public record EmailChangedResponse(
        String userId,
        String username,
        String email,
        boolean emailVerified
) {
}
