package pl.muybien.wallet.asset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AssetRequest(
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

        @NotNull(message = "Customer should be present")
        Long customerId
) {
}
