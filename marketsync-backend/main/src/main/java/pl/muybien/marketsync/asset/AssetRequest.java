package pl.muybien.marketsync.asset;

import lombok.NonNull;

import java.math.BigDecimal;

public record AssetRequest(
        @NonNull BigDecimal count
) {
    public AssetRequest {
        if (count.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Count must be greater than zero.");
        }
    }
}
