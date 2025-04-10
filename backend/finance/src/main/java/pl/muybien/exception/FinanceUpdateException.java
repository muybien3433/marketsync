package pl.muybien.exception;

public class FinanceUpdateException extends RuntimeException {
    public FinanceUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
