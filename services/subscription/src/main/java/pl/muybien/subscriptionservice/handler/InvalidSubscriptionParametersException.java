package pl.muybien.subscriptionservice.handler;

public class InvalidSubscriptionParametersException extends RuntimeException {
    public InvalidSubscriptionParametersException(String message) {
        super(message);
    }
}
