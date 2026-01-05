package pl.muybien.security;

import pl.muybien.dto.wallet.AssetAggregateDTO;
import pl.muybien.dto.wallet.AssetHistoryDTO;
import pl.muybien.enumeration.CurrencyType;
import pl.muybien.dto.wallet.request.AssetRequest;

import java.util.List;
import java.util.UUID;

public interface AssetService {

    void createAsset(UUID customerId, AssetRequest request);
    AssetHistoryDTO updateAsset(UUID customerId, AssetRequest request, UUID assetId);
    void deleteAsset(UUID customerId, UUID assetId);
    List<AssetHistoryDTO> findAllAssetHistory(UUID customerId);
    List<AssetAggregateDTO> findAllCustomerAssets(UUID customerId, CurrencyType desiredCurrency);
}
