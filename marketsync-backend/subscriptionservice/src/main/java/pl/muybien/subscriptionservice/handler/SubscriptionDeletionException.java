package pl.muybien.subscriptionservice.handler;

public class SubscriptionDeletionException extends RuntimeException {
    public SubscriptionDeletionException(String message) {
        super(message);
    }
}
