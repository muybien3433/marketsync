package pl.muybien.exception;

import lombok.Getter;

@Getter
public class PasswordChangeException extends RuntimeException {

    private final int status;

    public PasswordChangeException(String message, int status) {
        super(message);
        this.status = status;
    }
}
