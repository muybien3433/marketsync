package pl.muybien.exception;

import lombok.Getter;

@Getter
public class IamLoginException extends RuntimeException {

    private final int status;

    public IamLoginException(String message, int status) {
        super(message);
        this.status = status;
    }

}
