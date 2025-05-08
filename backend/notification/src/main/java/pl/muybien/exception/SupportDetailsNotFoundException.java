package pl.muybien.exception;

public class SupportDetailsNotFoundException extends RuntimeException {
    public SupportDetailsNotFoundException(String message) {
        super(message);
    }
}
