package io.platform.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

public record FeignErrorDecoder(
        ObjectMapper objectMapper,
        String upstreamService
) implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();

        ErrorResponse err = read(response);
        if (err != null) {
            return new UpstreamServiceException(status, err.code(), err.error(), upstreamService);
        }

        return new UpstreamServiceException(status, "UPSTREAM_ERROR", "Upstream call failed", upstreamService);
    }

    private ErrorResponse read(Response response) {
        try {
            if (response.body() == null) return null;
            try (InputStream is = response.body().asInputStream()) {
                return objectMapper.readValue(is, ErrorResponse.class);
            }
        } catch (Exception e) {
            return null;
        }
    }
}
