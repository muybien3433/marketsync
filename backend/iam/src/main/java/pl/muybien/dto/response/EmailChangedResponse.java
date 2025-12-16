package pl.muybien.dto.response;

public record EmailChangedResponse(
        String userId,
        String username,
        String email,
        boolean emailVerified
) {
}
