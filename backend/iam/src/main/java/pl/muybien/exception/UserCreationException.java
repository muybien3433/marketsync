package pl.muybien.exception;

import lombok.Getter;

@Getter
public class UserCreationException extends RuntimeException {

    private final int status;

    public UserCreationException(String message, int status) {
        super(message);
        this.status = status;
    }
}
