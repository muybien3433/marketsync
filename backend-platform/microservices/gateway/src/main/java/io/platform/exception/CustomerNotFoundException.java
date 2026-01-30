package io.platform.exception;

public class CustomerNotFoundException extends ServiceException {
    public CustomerNotFoundException(String message) {
        super(404, "CUSTOMER_NOT_FOUND", message);
    }
}
