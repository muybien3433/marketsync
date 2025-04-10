package pl.muybien.exception;

public class FinanceNotFoundException extends RuntimeException {
    public FinanceNotFoundException(String message) {
        super(message);
    }
}
