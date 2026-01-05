package pl.muybien.exception;

import lombok.Getter;

@Getter
public abstract class ServiceException extends RuntimeException {

    private final int status;
    private final String code;

    protected ServiceException(int status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    protected ServiceException(int status, String code, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.code = code;
    }
}
