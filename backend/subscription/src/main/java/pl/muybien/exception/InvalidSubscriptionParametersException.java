package pl.muybien.exception;

import io.platform.exception.ServiceException;

public class InvalidSubscriptionParametersException extends ServiceException {
    public InvalidSubscriptionParametersException(String message) {
        super(400, "SUBSCRIPTION_INVALID_PARAMETERS", message);
    }
}
