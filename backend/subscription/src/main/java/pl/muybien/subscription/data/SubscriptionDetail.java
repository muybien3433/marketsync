package pl.muybien.subscription.data;

import pl.muybien.enums.AssetType;
import pl.muybien.enums.CurrencyType;
import pl.muybien.enums.NotificationType;

import java.time.LocalDateTime;

public record SubscriptionDetail(
        String id,
        String uri,
        String customerId,
        String target,
        String financeName,
        CurrencyType requestedCurrency,
        Double upperBoundPrice,
        Double lowerBoundPrice,
        AssetType assetType,
        NotificationType notificationType,
        LocalDateTime createdDate
) {
}

