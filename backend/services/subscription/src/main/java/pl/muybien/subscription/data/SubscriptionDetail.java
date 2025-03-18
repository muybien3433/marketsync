package pl.muybien.subscription.data;

import pl.muybien.subscription.AssetType;
import pl.muybien.subscription.NotificationType;

import java.time.LocalDateTime;

public record SubscriptionDetail(
        String id,
        String uri,
        String customerId,
        String target,
        String financeName,
        pl.muybien.subscription.CurrencyType requestedCurrency,
        Double upperBoundPrice,
        Double lowerBoundPrice,
        AssetType assetType,
        NotificationType notificationType,
        LocalDateTime createdDate
) {
}

