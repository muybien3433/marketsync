package pl.muybien.asset;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record AssetHistoryDTO(
        Long id,
        String name,
        AssetType assetType,
        BigDecimal count,
        BigDecimal purchasePrice,
        LocalDateTime createdDate
) {
}