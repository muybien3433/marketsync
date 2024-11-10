package pl.muybien.subscriptionservice.subscription;

public record SubscriptionRequest(
        Double upperValueInPercent,
        Double lowerValueInPercent
) {
}
