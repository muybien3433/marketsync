package io.platform.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.Response;
import feign.codec.ErrorDecoder;
import feign.Util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;
    private final String upstreamService;

    public FeignErrorDecoder(ObjectMapper objectMapper, String upstreamService) {
        this.objectMapper = objectMapper;
        this.upstreamService = upstreamService;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        Request request = response.request();

        String url = request != null ? request.url() : "<no-url>";
        String httpMethod = request != null ? String.valueOf(request.httpMethod()) : "<no-method>";

        byte[] bodyBytes = readBodyBytes(response);
        ErrorResponse parsed = tryParse(bodyBytes);

        if (parsed != null) {
            return new UpstreamServiceException(status, parsed.code(), parsed.error(), upstreamService);
        }

        String rawBody = bodyBytes == null ? "" : new String(bodyBytes, StandardCharsets.UTF_8);
        rawBody = truncate(rawBody, 2000);

        String msg = "Upstream call failed: " + httpMethod + " " + url +
                " status=" + status +
                " methodKey=" + methodKey +
                (rawBody.isEmpty() ? "" : " body=" + rawBody);

        return new UpstreamServiceException(status, "UPSTREAM_ERROR", msg, upstreamService);
    }

    private byte[] readBodyBytes(Response response) {
        try {
            if (response.body() == null) return null;
            return Util.toByteArray(response.body().asInputStream());
        } catch (IOException e) {
            return null;
        }
    }

    private ErrorResponse tryParse(byte[] bodyBytes) {
        if (bodyBytes == null || bodyBytes.length == 0) return null;
        try {
            return objectMapper.readValue(bodyBytes, ErrorResponse.class);
        } catch (Exception e) {
            return null;
        }
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, max) + "...";
    }
}
