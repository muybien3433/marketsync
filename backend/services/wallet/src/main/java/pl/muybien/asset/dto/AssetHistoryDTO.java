package pl.muybien.asset.dto;

import lombok.Builder;
import pl.muybien.asset.AssetType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record AssetHistoryDTO(
        Long id,
        String name,
        String symbol,
        BigDecimal count,
        String currency,
        BigDecimal purchasePrice,
        LocalDateTime createdDate,
        AssetType assetType
) {
}