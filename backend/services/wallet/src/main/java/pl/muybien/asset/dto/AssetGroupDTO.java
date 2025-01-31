package pl.muybien.asset.dto;

import lombok.Builder;
import pl.muybien.asset.AssetType;

import java.math.BigDecimal;

@Builder
public record AssetGroupDTO(
        String name,
        String symbol,
        String uri,
        AssetType assetType,
        BigDecimal count,
        Double averagePurchasePrice,
        String currency,
        String customerId
) {
}
