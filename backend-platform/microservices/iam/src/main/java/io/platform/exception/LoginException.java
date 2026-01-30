package io.platform.exception;

public class LoginException extends ServiceException {

    public LoginException(int status, String code, String message) {
        super(status, code, message);
    }

    public LoginException(int status, String code, String message, Throwable cause) {
        super(status, code, message, cause);
    }
}
