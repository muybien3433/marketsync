package io.platform.exception;

public class RoleException extends ServiceException {

    public RoleException(int status, String code, String message) {
        super(status, code, message);
    }

    public RoleException(int status, String code, String message, Throwable cause) {
        super(status, code, message, cause);
    }
}
