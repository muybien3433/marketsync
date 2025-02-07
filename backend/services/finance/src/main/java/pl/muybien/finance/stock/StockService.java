package pl.muybien.finance.stock;

import pl.muybien.finance.AssetType;
import pl.muybien.finance.FinanceResponse;

public interface StockService {
    FinanceResponse fetchStock(String uri, AssetType assetType);
}
