package io.platform.exception;

public class UserCreationException extends ServiceException {

    public UserCreationException(int status, String message) {
        super(status, "USER_CREATION_FAILED", message);
    }

    public UserCreationException(int status, String code, String message) {
        super(status, code, message);
    }

    public UserCreationException(int status, String message, Throwable cause) {
        super(status, "USER_CREATION_FAILED", message, cause);
    }
}
