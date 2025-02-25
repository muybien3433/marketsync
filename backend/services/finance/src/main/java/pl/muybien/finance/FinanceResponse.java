package pl.muybien.finance;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record FinanceResponse(
        @JsonProperty("name") String name,
        @JsonProperty("symbol")String symbol,
        @JsonProperty("price")BigDecimal price,
        @JsonProperty("currency")CurrencyType currency,
        @JsonProperty("assetType")AssetType assetType
) {
}