package pl.muybien.core.error;

public class NotRetryableException extends RuntimeException {
    public NotRetryableException(Exception e) {
        super(e);
    }

    public NotRetryableException(String message) {
        super(message);
    }
}
