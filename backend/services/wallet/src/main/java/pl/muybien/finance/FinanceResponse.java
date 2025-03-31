package pl.muybien.finance;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record FinanceResponse(
        @JsonProperty("name") String name,
        @JsonProperty("symbol") String symbol,
        @JsonProperty("uri") String uri,
        @JsonProperty("unitType") String unitType,
        @JsonProperty("price") String price,
        @JsonProperty("currencyType") String currencyType,
        @JsonProperty("assetType") String assetType,
        @JsonProperty("lastUpdated") LocalDateTime lastUpdated
) {
}