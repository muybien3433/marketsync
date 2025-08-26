package pl.muybien.asset.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.muybien.enums.AssetType;
import pl.muybien.enums.CurrencyType;
import pl.muybien.enums.UnitType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AssetHistoryDTO(
        @JsonProperty("id") UUID id,
        @JsonProperty("name") String name,
        @JsonProperty("uri") String uri,
        @JsonProperty("symbol") String symbol,
        @JsonProperty("count") BigDecimal count,
        @JsonProperty("currencyType") CurrencyType currencyType,
        @JsonProperty("purchasePrice") BigDecimal purchasePrice,
        @JsonProperty("currentPrice") BigDecimal currentPrice,
        @JsonProperty("createdDate") LocalDateTime createdDate,
        @JsonProperty("assetType") AssetType assetType,
        @JsonProperty("unitType") UnitType unitType,
        @JsonProperty("comment") String comment
) {
}