package pl.muybien.exception;

import io.platform.exception.ServiceException;

public class FinanceNotFoundException extends ServiceException {
    public FinanceNotFoundException(String message) {
        super(404, "FINANCE_NOT_FOUND", message);
    }
}
