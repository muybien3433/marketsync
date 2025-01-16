package pl.muybien.asset;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AssetGroupDTO(
        String name,
        String uri,
        AssetType assetType,
        BigDecimal count,
        Double averagePurchasePrice,
        String currency,
        String customerId
) {
}
