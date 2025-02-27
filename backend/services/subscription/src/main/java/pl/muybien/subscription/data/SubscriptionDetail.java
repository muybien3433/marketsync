package pl.muybien.subscription.data;

import java.time.LocalDateTime;

public record SubscriptionDetail(
        String id,
        String uri,
        String customerId,
        String customerEmail,
        String financeName,
        String requestedCurrency,
        Double upperBoundPrice,
        Double lowerBoundPrice,
        String assetType,
        String notificationType,
        LocalDateTime createdDate
) {
}

