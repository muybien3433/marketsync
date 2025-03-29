package pl.muybien.asset.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.muybien.asset.AssetType;
import pl.muybien.asset.CurrencyType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AssetHistoryDTO(
        @JsonProperty("id") Long id,
        @JsonProperty("name") String name,
        @JsonProperty("symbol") String symbol,
        @JsonProperty("count") BigDecimal count,
        @JsonProperty("currencyType") CurrencyType currencyType,
        @JsonProperty("purchasePrice") BigDecimal purchasePrice,
        @JsonProperty("createdDate") LocalDateTime createdDate,
        @JsonProperty("assetType") AssetType assetType
) {
}