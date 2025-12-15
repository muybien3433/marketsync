package pl.muybien.dto.response;

public record StandardErrorResponse(
        String code,
        String message
) {
}