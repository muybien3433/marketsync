package pl.muybien.subscriptionservice.handler;

public class SubscriptionOwnershipException extends RuntimeException {
    public SubscriptionOwnershipException(String message) {
        super(message);
    }
}
