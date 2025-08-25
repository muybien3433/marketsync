package pl.muybien.finance;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.muybien.enums.AssetType;
import pl.muybien.enums.CurrencyType;
import pl.muybien.enums.UnitType;

import java.time.LocalDateTime;

public record FinanceResponse(
        @JsonProperty("name") String name,
        @JsonProperty("symbol") String symbol,
        @JsonProperty("uri") String uri,
        @JsonProperty("unitType") UnitType unitType,
        @JsonProperty("price") String price,
        @JsonProperty("currencyType") CurrencyType currencyType,
        @JsonProperty("assetType") AssetType assetType,
        @JsonProperty("lastUpdated") LocalDateTime lastUpdated
) {
}
