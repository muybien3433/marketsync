package pl.muybien.marketsync.asset;

import org.springframework.stereotype.Component;

@Component
public class AssetDTOMapper {
    AssetDTO mapToDTO(Asset asset) {
        return AssetDTO.builder()
                .name(asset.getName())
                .value(asset.getValue())
                .count(asset.getCount())
                .averagePurchasePrice(asset.getAveragePurchasePrice())
                .investmentPeriodInDays(asset.getInvestmentPeriodInDays())
                .build();
    }
}