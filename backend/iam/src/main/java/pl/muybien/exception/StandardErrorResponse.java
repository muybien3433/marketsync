package pl.muybien.exception;

public record StandardErrorResponse(
        String code,
        String message
) {
}