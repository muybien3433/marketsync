package pl.muybien.exception;

public class TransferServiceException extends RuntimeException {
    public TransferServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}