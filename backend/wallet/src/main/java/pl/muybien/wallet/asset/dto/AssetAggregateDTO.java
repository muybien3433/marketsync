package pl.muybien.wallet.asset.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.muybien.enumeration.AssetType;
import pl.muybien.enumeration.CurrencyType;
import pl.muybien.enumeration.UnitType;

import java.math.BigDecimal;

public record AssetAggregateDTO(
        @JsonProperty("name") String name,
        @JsonProperty("symbol") String symbol,
        @JsonProperty("uri") String uri,
        @JsonProperty("assetType") AssetType assetType,
        @JsonProperty("unitType") UnitType unitType,
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