package pl.muybien.finance.crypto;

import pl.muybien.finance.AssetType;
import pl.muybien.finance.FinanceResponse;

public interface CryptoService {
    void updateAvailableFinanceList();
    FinanceResponse fetchCrypto(String uri, AssetType assetType);
}
