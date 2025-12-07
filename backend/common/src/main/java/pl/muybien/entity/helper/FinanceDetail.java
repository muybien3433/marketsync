package pl.muybien.entity.helper;

import pl.muybien.enumeration.AssetType;
import pl.muybien.enumeration.CurrencyType;
import pl.muybien.enumeration.UnitType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FinanceDetail(
        String name,
        String symbol,
        String uri,
        UnitType unitType,
        BigDecimal price,
        CurrencyType currencyType,
        AssetType assetType,
        LocalDateTime lastUpdated,
        Integer position
) {
}
