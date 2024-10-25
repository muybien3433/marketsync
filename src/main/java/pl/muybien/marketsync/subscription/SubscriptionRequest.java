package pl.muybien.marketsync.subscription;

public record SubscriptionRequest(
        String uri,
        Double upperValueInPercent,
        Double lowerValueInPercent
) {
}
