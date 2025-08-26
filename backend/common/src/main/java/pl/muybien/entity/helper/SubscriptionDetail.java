package pl.muybien.entity.helper;

import pl.muybien.enumeration.AssetType;
import pl.muybien.enumeration.CurrencyType;
import pl.muybien.enumeration.NotificationType;

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

