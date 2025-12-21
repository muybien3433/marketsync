package pl.muybien.exception;

public class SubscriptionNotFoundException extends ServiceException {
    public SubscriptionNotFoundException(String message) {
        super(404, "SUBSCRIPTION_NOT_FOUND", message);
    }
}
