package pl.muybien.wallet.asset;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record AssetHistoryDTO(
        Long id,
        String name,
        AssetType type,
        BigDecimal count,
        BigDecimal purchasePrice,
        LocalDateTime createdDate
) {
}