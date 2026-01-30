package pl.muybien.exception;

import io.platform.exception.ServiceException;

public class CustomerNotFoundException extends ServiceException {
    public CustomerNotFoundException(String message) {
        super(404, "CUSTOMER_NOT_FOUND", message);
    }
}
