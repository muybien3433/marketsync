package pl.muybien.dto.iam.response;

public record UserLoginResponse(
        String access_token,
        String refresh_token,
        String token_type,
        long expires_in,
        long refresh_expires_in
) {
}
