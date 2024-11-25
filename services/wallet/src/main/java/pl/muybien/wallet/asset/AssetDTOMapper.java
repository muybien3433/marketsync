package pl.muybien.wallet.asset;

import org.springframework.stereotype.Component;

@Component
public class AssetDTOMapper {
    AssetDTO mapToDTO(Asset asset) {
        return AssetDTO.builder()
                .name(asset.getName())
                .count(asset.getCount())
                .averagePurchasePrice(asset.getAveragePurchasePrice())
                .investmentStartDate(asset.getInvestmentStartDate())
                .build();
    }
}