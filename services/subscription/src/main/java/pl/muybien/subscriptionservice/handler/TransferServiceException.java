package pl.muybien.subscriptionservice.handler;

public class TransferServiceException extends RuntimeException {
    public TransferServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
