package pl.muybien.finance.crypto;

import pl.muybien.finance.FinanceResponse;

public interface CryptoService {
    void updateAvailableFinanceList();
    FinanceResponse fetchCrypto(String uri, String assetType, String currency);
    FinanceResponse fetchCrypto(String uri, String assetType);
}
