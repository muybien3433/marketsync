package pl.muybien.core.error;

public class RetryableException extends RuntimeException {
    public RetryableException(Exception e) {
        super(e);
    }

    public RetryableException(String message) {
        super(message);
    }
}
