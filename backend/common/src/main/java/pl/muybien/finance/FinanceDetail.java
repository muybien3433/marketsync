package pl.muybien.finance;

import pl.muybien.enums.AssetType;
import pl.muybien.enums.CurrencyType;
import pl.muybien.enums.UnitType;

import java.time.LocalDateTime;

public record FinanceDetail(
        String name,
        String symbol,
        String uri,
        UnitType unitType,
        String price,
        CurrencyType currencyType,
        AssetType assetType,
        LocalDateTime lastUpdated
) {
}
