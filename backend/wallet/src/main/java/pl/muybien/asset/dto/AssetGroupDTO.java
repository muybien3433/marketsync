package pl.muybien.asset.dto;

import pl.muybien.enums.AssetType;
import pl.muybien.enums.CurrencyType;
import pl.muybien.enums.UnitType;

import java.math.BigDecimal;
import java.util.UUID;

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
        UUID customerId
) {
}
