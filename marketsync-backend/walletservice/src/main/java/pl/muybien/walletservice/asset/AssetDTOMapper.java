package pl.muybien.walletservice.asset;

import org.springframework.stereotype.Component;

@Component
public class AssetDTOMapper {
    AssetDTO mapToDTO(Asset asset) {
        return AssetDTO.builder()
                .name(asset.getName())
                .value(asset.getValue())
                .count(asset.getCount())
                .averagePurchasePrice(asset.getAveragePurchasePrice())
                .investmentStartDate(asset.getInvestmentStartDate())
                .build();
    }
}