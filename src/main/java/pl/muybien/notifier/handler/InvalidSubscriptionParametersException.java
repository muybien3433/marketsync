package pl.muybien.notifier.handler;

public class InvalidSubscriptionParametersException extends RuntimeException {
    public InvalidSubscriptionParametersException(String message) {
        super(message);
    }
}
