package pl.muybien.finance.crypto;

import pl.muybien.finance.FinanceResponse;

public interface CryptoService {
    FinanceResponse fetchCrypto(String uri, String type, String currency);
}
