package pl.muybien.finance;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalTime;

public record FinanceResponse(
        @JsonProperty("name") String name,
        @JsonProperty("symbol") String symbol,
        @JsonProperty("uri") String uri,
        @JsonProperty("price") BigDecimal price,
        @JsonProperty("currency") String currency,
        @JsonProperty("assetType") String assetType,
        @JsonProperty("lastUpdated") LocalTime lastUpdated
) {
}