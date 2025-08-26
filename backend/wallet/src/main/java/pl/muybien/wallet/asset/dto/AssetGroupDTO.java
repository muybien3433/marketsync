package pl.muybien.wallet.asset.dto;

import pl.muybien.enumeration.AssetType;
import pl.muybien.enumeration.CurrencyType;
import pl.muybien.enumeration.UnitType;

import java.math.BigDecimal;

public record AssetGroupDTO(
        String name,
        String symbol,
        String uri,
        AssetType assetType,
        UnitType unitType,
        BigDecimal count,
        BigDecimal averagePurchasePrice,
        BigDecimal currentPrice,
        CurrencyType currencyType,
        String customerId
) {
}
