package pl.muybien.mapper.wallet;

import org.springframework.stereotype.Component;
import pl.muybien.entity.wallet.Asset;
import pl.muybien.dto.wallet.AssetAggregateDTO;
import pl.muybien.dto.wallet.AssetGroupDTO;
import pl.muybien.dto.wallet.AssetHistoryDTO;

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
