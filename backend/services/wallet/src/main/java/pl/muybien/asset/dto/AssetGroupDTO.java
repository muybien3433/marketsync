package pl.muybien.asset.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.muybien.asset.AssetType;
import pl.muybien.asset.CurrencyType;

import java.math.BigDecimal;

public record AssetGroupDTO(
        @JsonProperty("name") String name,
        @JsonProperty("symbol") String symbol,
        @JsonProperty("uri") String uri,
        @JsonProperty("assetType") AssetType assetType,
        @JsonProperty("count") BigDecimal count,
        @JsonProperty("averagePurchasePrice") BigDecimal averagePurchasePrice,
        @JsonProperty("currencyType") CurrencyType currencyType,
        @JsonProperty("customerId") String customerId
) {
}
