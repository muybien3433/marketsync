package pl.muybien.marketsync.subscription;

public record SubscriptionRequest(
        Double upperValueInPercent,
        Double lowerValueInPercent
) {
}
