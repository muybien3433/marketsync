package pl.muybien.asset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import pl.muybien.enums.AssetType;
import pl.muybien.enums.CurrencyType;
import pl.muybien.enums.UnitType;

import java.math.BigDecimal;

public record AssetRequest(

        @NotNull(message = "Asset type is required")
        @NotEmpty(message = "Asset type is required")
        @NotBlank(message = "Asset type is required")
        AssetType assetType,

        @NotNull(message = "Uri is required")
        @NotEmpty(message = "Uri is required")
        @NotBlank(message = "Uri is required")
        String uri,

        @Positive(message = "Count should be positive")
        @NotNull(message = "Count should be present")
        BigDecimal count,

        @NotNull(message = "Purchase price should be present")
        @Positive(message = "Purchase price should be positive")
        BigDecimal purchasePrice,

        @NotNull(message = "Currency type is required")
        @NotEmpty(message = "Currency type is required")
        @NotBlank(message = "Currency type is required")
        CurrencyType currencyType,

        String name,

        UnitType unitType,

        BigDecimal currentPrice,

        String comment
) {
}
