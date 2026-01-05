package pl.muybien.dto.iam.response;

public record StandardErrorResponse(
        String code,
        String message
) {
}