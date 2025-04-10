package pl.muybien.asset.dto;

import pl.muybien.enums.AssetType;
import pl.muybien.enums.CurrencyType;

import java.math.BigDecimal;

public record AssetGroupDTO(
        String name,
        String symbol,
        String uri,
        AssetType assetType,
        String unitType,
        BigDecimal count,
        BigDecimal averagePurchasePrice,
        BigDecimal currentPrice,
        CurrencyType currencyType,
        String customerId
) {
}
