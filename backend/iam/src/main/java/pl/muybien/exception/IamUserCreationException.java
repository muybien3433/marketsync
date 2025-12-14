package pl.muybien.exception;

import lombok.Getter;

@Getter
public class IamUserCreationException extends RuntimeException {

    private final int status;

    public IamUserCreationException(String message, int status) {
        super(message);
        this.status = status;
    }
}
