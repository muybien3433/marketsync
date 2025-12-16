package pl.muybien.exception;

import lombok.Getter;

@Getter
public class RoleException extends RuntimeException {

    private final String code;
    private final int status;

    public RoleException(String code, String message, int status) {
        super(message);
        this.code = code;
        this.status = status;
    }
}
