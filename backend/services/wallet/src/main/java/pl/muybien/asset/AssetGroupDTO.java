package pl.muybien.asset;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record AssetGroupDTO(
        String name,
        String uri,
        AssetType type,
        BigDecimal count,
        Double averagePurchasePrice,
        String currency,
        String customerId
) {
}
