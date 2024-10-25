package pl.muybien.marketsync.handler;

public class InvalidSubscriptionParametersException extends RuntimeException {
    public InvalidSubscriptionParametersException(String message) {
        super(message);
    }
}
