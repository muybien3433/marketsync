package pl.muybien.wallet.asset.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.muybien.enumeration.AssetType;
import pl.muybien.enumeration.CurrencyType;
import pl.muybien.enumeration.UnitType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AssetHistoryDTO(
        @JsonProperty("id") Long id,
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