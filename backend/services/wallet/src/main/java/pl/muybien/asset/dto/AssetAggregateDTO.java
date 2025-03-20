package pl.muybien.asset.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record AssetAggregateDTO(
        @JsonProperty("name") String name,
        @JsonProperty("symbol") String symbol,
        @JsonProperty("assetType") String assetType,
        @JsonProperty("count") BigDecimal count,
        @JsonProperty("currentPrice") BigDecimal currentPrice,
        @JsonProperty("currencyType") String currencyType,
        @JsonProperty("value") BigDecimal value,
        @JsonProperty("averagePurchasePrice") BigDecimal averagePurchasePrice,
        @JsonProperty("profit") BigDecimal profit,
        @JsonProperty("profitInPercentage") BigDecimal profitInPercentage,
        @JsonProperty("exchangeRateToDesired") BigDecimal exchangeRateToDesired
) {
}