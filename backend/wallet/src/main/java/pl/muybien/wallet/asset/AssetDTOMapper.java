package pl.muybien.wallet.asset;

import org.springframework.stereotype.Component;
import pl.muybien.entity.Asset;
import pl.muybien.wallet.asset.dto.AssetAggregateDTO;
import pl.muybien.wallet.asset.dto.AssetGroupDTO;
import pl.muybien.wallet.asset.dto.AssetHistoryDTO;

import java.math.BigDecimal;

import static pl.muybien.util.PriceUtil.normalizePrice;

@Component
public class AssetDTOMapper {

    public AssetHistoryDTO toAssetHistoryDTO(Asset asset) {
        return new AssetHistoryDTO(
                asset.getId(),
                asset.getName(),
                asset.getUri(),
                asset.getSymbol(),
                normalizePrice(asset.getCount()),
                asset.getCurrencyType(),
                normalizePrice(asset.getPurchasePrice()),
                normalizePrice(asset.getCurrentPrice()),
                asset.getCreatedDate(),
                asset.getAssetType(),
                asset.getUnitType(),
                asset.getComment()
        );
    }

    public AssetAggregateDTO toAssetAggregateDTO(
            AssetGroupDTO asset,
            BigDecimal currentPrice,
            BigDecimal value,
            BigDecimal profit,
            BigDecimal profitPercentage,
            BigDecimal exchangeRateToDesired
    ) {
        return new AssetAggregateDTO(
                asset.name(),
                asset.symbol(),
                asset.uri(),
                asset.assetType(),
                asset.unitType(),
                normalizePrice(asset.count()),
                normalizePrice(currentPrice),
                asset.currencyType(),
                normalizePrice(value),
                normalizePrice(asset.averagePurchasePrice()),
                normalizePrice(profit),
                normalizePrice(profitPercentage),
                exchangeRateToDesired
        );
    }
}
