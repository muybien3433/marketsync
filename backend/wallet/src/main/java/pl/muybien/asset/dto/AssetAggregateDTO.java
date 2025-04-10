package pl.muybien.asset.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.muybien.enums.AssetType;
import pl.muybien.enums.CurrencyType;

import java.math.BigDecimal;

public record AssetAggregateDTO(
        @JsonProperty("name") String name,
        @JsonProperty("symbol") String symbol,
        @JsonProperty("assetType") AssetType assetType,
        @JsonProperty("unitType") String unitType,
        @JsonProperty("count") BigDecimal count,
        @JsonProperty("currentPrice") BigDecimal currentPrice,
        @JsonProperty("currencyType") CurrencyType currencyType,
        @JsonProperty("value") BigDecimal value,
        @JsonProperty("averagePurchasePrice") BigDecimal averagePurchasePrice,
        @JsonProperty("profit") BigDecimal profit,
        @JsonProperty("profitInPercentage") BigDecimal profitInPercentage,
        @JsonProperty("exchangeRateToDesired") BigDecimal exchangeRateToDesired
) {
}