package pl.muybien.finance.exception;

public class FinanceNotFoundException extends RuntimeException {
    public FinanceNotFoundException(String message) {
        super(message);
    }
}
