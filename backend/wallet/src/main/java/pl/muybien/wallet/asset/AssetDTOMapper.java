package pl.muybien.wallet.asset;

import org.springframework.stereotype.Component;
import pl.muybien.entity.Asset;
import pl.muybien.wallet.asset.dto.AssetHistoryDTO;

@Component
public class AssetDTOMapper {

    public AssetHistoryDTO toAssetHistoryDTO(Asset asset) {
        return new AssetHistoryDTO(
                asset.getId(),
                asset.getName(),
                asset.getUri(),
                asset.getSymbol(),
                asset.getCount(),
                asset.getCurrencyType(),
                asset.getPurchasePrice(),
                asset.getCurrentPrice(),
                asset.getCreatedDate(),
                asset.getAssetType(),
                asset.getUnitType(),
                asset.getComment()
        );
    }
}
