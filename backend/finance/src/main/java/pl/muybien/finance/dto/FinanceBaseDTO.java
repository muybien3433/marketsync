package pl.muybien.finance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.muybien.enumeration.AssetType;
import pl.muybien.enumeration.UnitType;

public record FinanceBaseDTO(
        @JsonProperty("name") String name,
        @JsonProperty("symbol") String symbol,
        @JsonProperty("uri") String uri,
        @JsonProperty("unitType") UnitType unitType,
        @JsonProperty("assetType") AssetType assetType
) {
}
