package pl.muybien.walletservice.asset;

import lombok.NonNull;

import java.math.BigDecimal;

public record AssetRequest(
        @NonNull BigDecimal count,
        @NonNull BigDecimal price
) {
    public AssetRequest {
        if (count.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Count must be greater than zero.");
        }

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }
    }
}
