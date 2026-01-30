package pl.muybien.exception;

import io.platform.exception.ServiceException;

public class FinanceNotFoundException extends ServiceException {

    public FinanceNotFoundException(String message) {
        super(
                404,
                "FINANCE_RATE_NOT_FOUND",
                message
        );
    }

    public FinanceNotFoundException(String message, Throwable cause) {
        super(
                404,
                "FINANCE_RATE_NOT_FOUND",
                message,
                cause
        );
    }
}
