package io.platform.dto.iam.response;

public record StandardErrorResponse(
        String code,
        String message
) {
}