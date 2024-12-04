package pl.muybien.subscription.exception;

public class InvalidSubscriptionParametersException extends RuntimeException {
    public InvalidSubscriptionParametersException(String message) {
        super(message);
    }
}
