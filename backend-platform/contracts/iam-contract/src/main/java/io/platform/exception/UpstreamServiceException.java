package io.platform.exception;

import lombok.Getter;

@Getter
public class UpstreamServiceException extends ServiceException {

    private final String upstreamService;

    public UpstreamServiceException(int status, String code, String message, String upstreamService) {
        super(status, code, message);
        this.upstreamService = upstreamService;
    }
}
