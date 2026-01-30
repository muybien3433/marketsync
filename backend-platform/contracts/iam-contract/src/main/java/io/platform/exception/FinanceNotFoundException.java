package io.platform.exception;

public class FinanceNotFoundException extends ServiceException {
    public FinanceNotFoundException(String message) {
        super(404, "FINANCE_NOT_FOUND", message);
    }
}
