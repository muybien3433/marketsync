package io.platform.exception;

public record ErrorResponse(
        String timestamp,
        int status,
        String error,
        String code,
        String path
) {
}