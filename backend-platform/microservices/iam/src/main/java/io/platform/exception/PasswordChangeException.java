package io.platform.exception;

public class PasswordChangeException extends ServiceException {

    public PasswordChangeException(int status, String code, String message) {
        super(status, code, message);
    }

    public PasswordChangeException(int status, String code, String message, Throwable cause) {
        super(status, code, message, cause);
    }
}
